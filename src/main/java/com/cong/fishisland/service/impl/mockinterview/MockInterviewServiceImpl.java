package com.cong.fishisland.service.impl.mockinterview;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cong.fishisland.common.ErrorCode;
import com.cong.fishisland.common.exception.BusinessException;
import com.cong.fishisland.common.exception.ThrowUtils;
import com.cong.fishisland.constant.CommonConstant;
import com.cong.fishisland.datasource.ai.MockInterviewDataSource;
import com.cong.fishisland.mapper.mockInterview.MockInterviewMapper;
import com.cong.fishisland.model.dto.mockInterview.MockInterviewAddRequest;
import com.cong.fishisland.model.dto.mockInterview.MockInterviewChatMessage;
import com.cong.fishisland.model.dto.mockInterview.MockInterviewEventRequest;
import com.cong.fishisland.model.dto.mockInterview.MockInterviewQueryRequest;
import com.cong.fishisland.model.entity.mockInterview.MockInterview;
import com.cong.fishisland.model.entity.user.User;
import com.cong.fishisland.model.enums.MockInterviewEventEnum;
import com.cong.fishisland.model.enums.MockInterviewStatusEnum;
import com.cong.fishisland.model.vo.ai.AiResponse;
import com.cong.fishisland.model.vo.ai.SiliconFlowRequest;
import com.cong.fishisland.service.MockInterviewService;
import com.cong.fishisland.service.UserService;
import com.cong.fishisland.utils.SqlUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import static com.cong.fishisland.datasource.ai.MockInterviewDataSource.DEFAULT_MODEL;

/**
 * @author Shing
 * @description 针对表【mock_interview(模拟面试)】的数据库操作Service实现
 * @createDate 2025-04-02 18:21:40
 */
@Service
public class MockInterviewServiceImpl extends ServiceImpl<MockInterviewMapper, MockInterview>
        implements MockInterviewService {

    @Resource
    private MockInterviewDataSource mockInterviewDataSource;

    @Resource
    private UserService userService;

    private final String systemPromptFormat = "你是一位严厉的程序员面试官，我是候选人，来应聘 %s 的 %s 岗位，面试难度为 %s。请你向我依次提出问题（最多 20 个问题），我也会依次回复。在这期间请完全保持真人面试官的口吻，比如适当引导学员、或者表达出你对学员回答的态度。\n" +
            "必须满足如下要求：\n" +
            "1. 当学员回复 “开始” 时，你要正式开始面试\n" +
            "2. 当学员表示希望 “结束面试” 时，你要结束面试\n" +
            "3. 此外，当你觉得这场面试可以结束时（比如候选人回答结果较差、不满足工作年限的招聘需求、或者候选人态度不礼貌），必须主动提出面试结束，不用继续询问更多问题了。并且要在回复中包含字符串【面试结束】\n" +
            "4. 面试结束后，应该给出候选人整场面试的表现和总结。";

    final SiliconFlowRequest.Message userStartMessage = new SiliconFlowRequest.Message();
    final SiliconFlowRequest.Message endUserMessage = new SiliconFlowRequest.Message();

    // 初始化块设置消息属性
    {
        userStartMessage.setRole("user");
        userStartMessage.setContent("开始");

        endUserMessage.setRole("user");
        endUserMessage.setContent("结束");
    }


    /**
     * 创建模拟面试
     */
    @Override
    public Long createMockInterview(MockInterviewAddRequest mockInterviewAddRequest) {
        User loginUser = userService.getLoginUser();
        // 1. 参数校验
        if (mockInterviewAddRequest == null || loginUser == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String workExperience = mockInterviewAddRequest.getWorkExperience();
        String jobPosition = mockInterviewAddRequest.getJobPosition();
        String difficulty = mockInterviewAddRequest.getDifficulty();
        ThrowUtils.throwIf(StrUtil.hasBlank(workExperience, jobPosition, difficulty), ErrorCode.PARAMS_ERROR, "参数错误");
        // 2. 封装插入到数据库中的对象
        MockInterview mockInterview = new MockInterview();
        mockInterview.setWorkExperience(workExperience);
        mockInterview.setJobPosition(jobPosition);
        mockInterview.setDifficulty(difficulty);
        mockInterview.setUserId(loginUser.getId());
        mockInterview.setStatus(MockInterviewStatusEnum.TO_START.getValue());
        // 3. 插入到数据库
        boolean result = this.save(mockInterview);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR, "创建失败");
        // 4. 返回 id
        return mockInterview.getId();
    }

    /**
     * 获取查询条件
     */
    @Override
    public QueryWrapper<MockInterview> getQueryWrapper(MockInterviewQueryRequest mockInterviewQueryRequest) {
        QueryWrapper<MockInterview> queryWrapper = new QueryWrapper<>();
        if (mockInterviewQueryRequest == null) {
            return queryWrapper;
        }
        // 从对象中取值
        Long id = mockInterviewQueryRequest.getId();
        String workExperience = mockInterviewQueryRequest.getWorkExperience();
        String jobPosition = mockInterviewQueryRequest.getJobPosition();
        String difficulty = mockInterviewQueryRequest.getDifficulty();
        Integer status = mockInterviewQueryRequest.getStatus();
        Long userId = mockInterviewQueryRequest.getUserId();
        String sortField = mockInterviewQueryRequest.getSortField();
        String sortOrder = mockInterviewQueryRequest.getSortOrder();
        // 补充需要的查询条件
        queryWrapper.eq(ObjectUtils.isNotEmpty(id), "id", id);
        queryWrapper.like(StringUtils.isNotBlank(workExperience), "workExperience", workExperience);
        queryWrapper.like(StringUtils.isNotBlank(jobPosition), "jobPosition", jobPosition);
        queryWrapper.like(StringUtils.isNotBlank(difficulty), "difficulty", difficulty);
        queryWrapper.eq(ObjectUtils.isNotEmpty(status), "status", status);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "userId", userId);
        // 排序规则
        queryWrapper.orderBy(SqlUtils.validSortField(sortField),
                sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }

    /**
     * 处理模拟面试事件
     */
    @Override
    public String handleMockInterviewEvent(MockInterviewEventRequest mockInterviewEventRequest) {
        User loginUser = userService.getLoginUser();
        // 区分事件
        Long id = mockInterviewEventRequest.getId();
        if (id == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数错误");
        }
        MockInterview mockInterview = this.getById(id);
        ThrowUtils.throwIf(mockInterview == null, ErrorCode.PARAMS_ERROR, "模拟面试未创建");
        // 如果不是本人创建的模拟面试，报错
        if (!mockInterview.getUserId().equals(loginUser.getId())) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        String event = mockInterviewEventRequest.getEvent();
        MockInterviewEventEnum eventEnum = MockInterviewEventEnum.getEnumByValue(event);
        switch (Objects.requireNonNull(eventEnum)) {
            // -- 处理开始事件
            // 用户进入模拟面试，发送“开始”事件，修改模拟面试的状态为“已开始”，AI 要给出对应的回复
            case START:
                return handleChatStartEvent(mockInterview);
            // -- 处理对话事件
            // 用户可以和 AI 面试官发送消息，发送“消息”事件，携带上要发送的消息内容，AI 要给出对应的回复
            case CHAT:
                return handleChatMessageEvent(mockInterviewEventRequest, mockInterview);
            case END:
                // -- 处理结束事件
                // 退出模拟面试，发送“退出”事件，AI 给出面试的复盘总结，修改状态为“已结束”
                return handleChatEndEvent(mockInterview);
            default:
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数错误");
        }
    }

    /**
     * 处理 AI 对话开始事件（开始对话时调用）
     */
    private String handleChatStartEvent(MockInterview mockInterview) {
        // 根据用户的工作经验、职位和难度生成系统提示
        String systemPrompt = String.format(systemPromptFormat,
                mockInterview.getWorkExperience(),
                mockInterview.getJobPosition(),
                mockInterview.getDifficulty());

        List<SiliconFlowRequest.Message> messages = new ArrayList<>();

        // 添加系统消息
        SiliconFlowRequest.Message systemMessage = new SiliconFlowRequest.Message();
        systemMessage.setRole("system");
        systemMessage.setContent(systemPrompt);
        messages.add(systemMessage);

        messages.add(userStartMessage);


        // 调用 AI 接口获取回复
        AiResponse aiResponse = mockInterviewDataSource.getAiResponse(messages,DEFAULT_MODEL);
        String answer = aiResponse.getAnswer();

        // 封装 AI 回复消息，添加至消息列表
        SiliconFlowRequest.Message assistantMessage = new SiliconFlowRequest.Message();
        assistantMessage.setRole("assistant");
        assistantMessage.setContent(answer);
        messages.add(assistantMessage);

        // 保存更新后的消息记录（转换为可持久化保存的格式）
        List<MockInterviewChatMessage> chatMessageList = transformFromSiliconFlowMessage(messages);
        String jsonStr = JSONUtil.toJsonStr(chatMessageList);

        // 更新面试记录状态为进行中，并保存消息记录
        MockInterview updateMockInterview = new MockInterview();
        updateMockInterview.setId(mockInterview.getId());
        updateMockInterview.setStatus(MockInterviewStatusEnum.IN_PROGRESS.getValue());
        updateMockInterview.setMessages(jsonStr);

        boolean result = this.updateById(updateMockInterview);
        ThrowUtils.throwIf(!result, ErrorCode.SYSTEM_ERROR, "更新失败");

        return answer;
    }

    /**
     * 处理 AI 对话消息事件（对话过程中用户发送消息时调用）
     */
    private String handleChatMessageEvent(MockInterviewEventRequest mockInterviewEventRequest, MockInterview mockInterview) {
        String message = mockInterviewEventRequest.getMessage();

        // 获取历史消息记录
        List<SiliconFlowRequest.Message> historyMessages = JSONUtil.parseArray(mockInterview.getMessages())
                .toList(MockInterviewChatMessage.class)
                .stream()
                .map(this::convertToSiliconFlowMessage)
                .collect(Collectors.toList());

        SiliconFlowRequest.Message userMessage = new SiliconFlowRequest.Message();
        userMessage.setRole("user");
        userMessage.setContent(message);
        historyMessages.add(userMessage);

        // 调用 AI 接口获取回复
        AiResponse aiResponse = mockInterviewDataSource.getAiResponse(historyMessages, DEFAULT_MODEL);
        String chatAnswer = aiResponse.getAnswer();

        // 封装 AI 的回复消息
        SiliconFlowRequest.Message assistantMessage = new SiliconFlowRequest.Message();
        assistantMessage.setRole("assistant");
        assistantMessage.setContent(chatAnswer);
        historyMessages.add(assistantMessage);

        // 更新消息记录，转换为可保存的格式并转换为 JSON 字符串
        List<MockInterviewChatMessage> chatMessages = transformFromSiliconFlowMessage(historyMessages);
        String newJsonStr = JSONUtil.toJsonStr(chatMessages);

        MockInterview newUpdateMockInterview = new MockInterview();
        newUpdateMockInterview.setId(mockInterview.getId());
        newUpdateMockInterview.setMessages(newJsonStr);

        // 如果 AI 主动结束了面试，则更新状态
        if (chatAnswer.contains("【面试结束】")) {
            newUpdateMockInterview.setStatus(MockInterviewStatusEnum.ENDED.getValue());
        }

        boolean newResult = this.updateById(newUpdateMockInterview);
        ThrowUtils.throwIf(!newResult, ErrorCode.SYSTEM_ERROR, "更新失败");

        return chatAnswer;
    }

    /**
     * 处理 AI 对话结束事件（对话结束时调用）
     */
    private String handleChatEndEvent(MockInterview mockInterview) {
        // 获取之前的消息记录
        String historyMessage = mockInterview.getMessages();
        List<MockInterviewChatMessage> historyMessageList = JSONUtil.parseArray(historyMessage)
                .toList(MockInterviewChatMessage.class);

        // 1.直接转换为 SiliconFlowRequest.Message 列表
        List<SiliconFlowRequest.Message> messages = historyMessageList.stream()
                .map(this::convertToSiliconFlowMessage)
                .collect(Collectors.toList());

        // 2.添加用户结束消息（使用预定义的 endUserMessage）
        messages.add(endUserMessage);

        // 调用 AI 接口获取答案（使用默认模型）
        AiResponse aiResponse = mockInterviewDataSource.getAiResponse(messages, DEFAULT_MODEL);
        String endAnswer = aiResponse.getAnswer();

        // 3.构建 AI 回复消息
        SiliconFlowRequest.Message assistantMessage = new SiliconFlowRequest.Message();
        assistantMessage.setRole("assistant");
        assistantMessage.setContent(endAnswer);
        messages.add(assistantMessage);

        // 转换持久化格式
        List<MockInterviewChatMessage> mockInterviewChatMessages = messages.stream()
                .map(msg -> {
                    MockInterviewChatMessage entity = new MockInterviewChatMessage();
                    entity.setRole(msg.getRole());
                    entity.setMessage(msg.getContent());
                    return entity;
                })
                .collect(Collectors.toList());

        String newJsonStr = JSONUtil.toJsonStr(mockInterviewChatMessages);

        // 更新面试记录状态为已结束，同时保存消息记录
        MockInterview newUpdateMockInterview = new MockInterview();
        newUpdateMockInterview.setId(mockInterview.getId());
        newUpdateMockInterview.setStatus(MockInterviewStatusEnum.ENDED.getValue());
        newUpdateMockInterview.setMessages(newJsonStr);

        boolean newResult = this.updateById(newUpdateMockInterview);
        ThrowUtils.throwIf(!newResult, ErrorCode.SYSTEM_ERROR, "更新失败");

        return endAnswer;
    }

    /**
     * 转换持久化消息格式
     */
    private List<MockInterviewChatMessage> transformFromSiliconFlowMessage(List<SiliconFlowRequest.Message> messages) {
        return messages.stream().map(msg -> {
            MockInterviewChatMessage entity = new MockInterviewChatMessage();
            entity.setRole(msg.getRole());
            entity.setMessage(msg.getContent());
            return entity;
        }).collect(Collectors.toList());
    }

    /**
     * 转换历史消息格式
     */
    private SiliconFlowRequest.Message convertToSiliconFlowMessage(MockInterviewChatMessage entity) {
        SiliconFlowRequest.Message msg = new SiliconFlowRequest.Message();
        msg.setRole(entity.getRole());
        msg.setContent(entity.getMessage());
        return msg;
    }

}




