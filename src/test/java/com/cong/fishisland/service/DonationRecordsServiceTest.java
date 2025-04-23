package com.cong.fishisland.service;

import com.cong.fishisland.common.ErrorCode;
import com.cong.fishisland.common.TestBase;
import com.cong.fishisland.common.exception.BusinessException;
import com.cong.fishisland.mapper.donation.DonationRecordsMapper;
import com.cong.fishisland.model.dto.donation.DonationRecordsAddRequest;
import com.cong.fishisland.model.entity.donation.DonationRecords;
import com.cong.fishisland.service.impl.donation.DonationRecordsServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

/**
 * @author Shing
 * date 23/4/2025
 */
class DonationRecordsServiceTest extends TestBase {

    @InjectMocks
    private DonationRecordsServiceImpl service;

    @Mock
    private DonationRecordsMapper mapper;

    @Mock
    private UserService userService;

    @BeforeEach
    void setUp() {
        // 把 mapper 注入到 ServiceImpl 的 baseMapper 字段
        ReflectionTestUtils.setField(service, "baseMapper", mapper);
    }

    /**
     * 测试创建打赏记录时，新用户的情况
     */
    @Test
    void TestCreateRecord_NewDonor() {
        // 场景1：donorId 不存在，应插入
        DonationRecordsAddRequest req = new DonationRecordsAddRequest();
        req.setDonorId(100L);
        req.setAmount(new BigDecimal("50.00"));
        req.setRemark("初次打赏");

        // mapper.selectByDonorIdForUpdate 返回 null，表示无历史记录
        when(mapper.selectByDonorIdForUpdate(100L)).thenReturn(null);
        // service.save(...) 调用父类方法，我们 spy 服务并 stub save
        DonationRecordsServiceImpl spyService = Mockito.spy(service);
        doReturn(true).when(spyService).save(ArgumentMatchers.any(DonationRecords.class));

        Long generatedId = 123L;
        // 模拟 save 后实体 id 被设置
        doAnswer(invocation -> {
            DonationRecords ent = invocation.getArgument(0);
            ent.setId(generatedId);
            return true;
        }).when(spyService).save(ArgumentMatchers.any(DonationRecords.class));

        Long result = spyService.createRecord(req);

        assertEquals(generatedId, result);
        // 验证插入调用，只调用 save 一次，不调用 updateById
        verify(spyService, times(1)).save(ArgumentMatchers.any(DonationRecords.class));
        verify(spyService, never()).updateById(any());
    }

    /**
     * 测试创建打赏记录时，已有记录的情况
     */
    @Test
    void TestCreateRecord_ExistingDonor() {
        // 场景2：donorId 已存在，应累加
        DonationRecordsAddRequest req = new DonationRecordsAddRequest();
        req.setDonorId(200L);
        req.setAmount(new BigDecimal("30.00"));
        req.setRemark("Thanks");

        DonationRecords existing = new DonationRecords();
        existing.setId(555L);
        existing.setDonorId(200L);
        existing.setAmount(new BigDecimal("20.00"));
        existing.setRemark("Old");

        when(mapper.selectByDonorIdForUpdate(200L)).thenReturn(existing);

        // spy 调用 updateById 返回 true
        DonationRecordsServiceImpl spyService = Mockito.spy(service);
        ReflectionTestUtils.setField(spyService, "baseMapper", mapper);
        doReturn(true).when(spyService).updateById(existing);

        Long result = spyService.createRecord(req);

        // 验证 amount 累加到 50.00，remark 被替换为新 remark
        assertEquals(555L, result);
        assertEquals(new BigDecimal("50.00"), existing.getAmount());
        assertEquals("Thanks", existing.getRemark());

        verify(spyService, times(1)).updateById(existing);
        verify(spyService, never()).save(any());
    }

    /**
     * 测试创建打赏记录时，金额非法的情况
     */
    @Test
    void TestCreateRecord_InvalidAmount() {
        DonationRecordsAddRequest req = new DonationRecordsAddRequest();
        req.setDonorId(300L);
        req.setAmount(new BigDecimal("-5.00"));

        when(mapper.selectByDonorIdForUpdate(300L)).thenReturn(null);

        DonationRecordsServiceImpl spyService = Mockito.spy(service);
        ReflectionTestUtils.setField(spyService, "baseMapper", mapper);

        BusinessException ex = assertThrows(BusinessException.class, () -> {
            spyService.createRecord(req);
        });
        assertEquals(ErrorCode.PARAMS_ERROR.getCode(), ex.getCode());
    }

    /**
     * 测试创建打赏记录时，并发情况
     */
    @Test
    void TestCreateRecord_Concurrent() throws InterruptedException {
        final long donorId = 999L;
        // 初始记录：10.00
        DonationRecords initial = new DonationRecords();
        initial.setId(1L);
        initial.setDonorId(donorId);
        initial.setAmount(new BigDecimal("10.00"));
        initial.setRemark("init");

        // 准备两个同步器：一个标记锁是否已被拿走；一个用于释放锁
        AtomicBoolean lockTaken = new AtomicBoolean(false);
        CountDownLatch lockReleased = new CountDownLatch(1);

        // 模拟悲观锁查询：第一个线程直接返回；其他线程阻塞直到 lockReleased.countDown()
        when(mapper.selectByDonorIdForUpdate(donorId)).thenAnswer(invocation -> {
            if (lockTaken.compareAndSet(false, true)) {
                // 第一个线程拿到“锁”
                return initial;
            } else {
                // 后续线程等待“锁”释放
                lockReleased.await();
                return initial;
            }
        });

        // Spy 服务，实现 updateById 时释放锁
        DonationRecordsServiceImpl spyService = spy(service);
        ReflectionTestUtils.setField(spyService, "baseMapper", mapper);
        // save 不影响并发累加逻辑，这里直接返回 true
        doReturn(true).when(spyService).save(any(DonationRecords.class));
        // 当第一个线程执行 updateById 时，释放锁
        doAnswer(invocation -> {
            // 释放锁，让其他阻塞线程继续
            lockReleased.countDown();
            return true;
        }).when(spyService).updateById(any(DonationRecords.class));

        int threads = 5;
        BigDecimal tip = new BigDecimal("2.00");
        ExecutorService exec = Executors.newFixedThreadPool(threads);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(threads);

        // 并发提交多个打赏，每人 2.00
        for (int i = 0; i < threads; i++) {
            exec.submit(() -> {
                try {
                    startLatch.await();
                    DonationRecordsAddRequest req = new DonationRecordsAddRequest();
                    req.setDonorId(donorId);
                    req.setAmount(tip);
                    req.setRemark(null);
                    spyService.createRecord(req);
                } catch (Exception ignored) {
                } finally {
                    doneLatch.countDown();
                }
            });
        }

        // 同时发起
        startLatch.countDown();
        doneLatch.await();
        exec.shutdown();

        // 最终 amount 应为 10 + threads*2 = 10 + 5*2 = 20.00
        assertEquals(0, initial.getAmount().compareTo(new BigDecimal("20.00")));
    }
}