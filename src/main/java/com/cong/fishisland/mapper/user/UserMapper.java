package com.cong.fishisland.mapper.user;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.cong.fishisland.model.entity.user.User;
import com.cong.fishisland.model.vo.user.NewUserDataWebVO;
import com.cong.fishisland.model.vo.user.UserDataWebVO;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * 用户数据库操作
 * # @author <a href="https://github.com/lhccong">程序员聪</a>
 */
public interface UserMapper extends BaseMapper<User> {

    /**
     * 首页-用户数据
     * @param wrapper 查询条件
     * @return 用户数据
     */
    UserDataWebVO getUserDataWebVO(@Param(Constants.WRAPPER) QueryWrapper<User> wrapper);

    /**
     * 获取每周新增用户数据
     * @return 新增用户数据
     */
    List<NewUserDataWebVO> getNewUserDataWebVOEveryWeek();

    /**
     * 获取每月新增用户数据
     * @return 新增用户数据
     */
    List<NewUserDataWebVO> getNewUserDataWebVOEveryMonth();

    /**
     * 获取每年新增用户数据
     * @return 新增用户数据
     */
    List<NewUserDataWebVO> getNewUserDataWebVOEveryYear();

    /**
     * 获取时间段内新增用户数据
     * @return 新增用户数据
     */
    List<NewUserDataWebVO> getNewUserDataWebVOByTime(@Param("beginTime")Date beginTime,@Param("endTime") Date endTime);
}




