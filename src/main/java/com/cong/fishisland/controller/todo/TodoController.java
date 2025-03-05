package com.cong.fishisland.controller.todo;

import cn.dev33.satoken.stp.StpUtil;
import com.cong.fishisland.common.BaseResponse;
import com.cong.fishisland.common.ResultUtils;
import com.cong.fishisland.model.dto.todo.SaveTodoDto;
import com.cong.fishisland.service.TodoService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 待办数据接口
 * # @author <a href="https://github.com/lhccong">程序员聪</a>
 */
@RestController
@RequestMapping("/todo")
@Slf4j
@RequiredArgsConstructor
//@Api(tags = "待办数据")
public class TodoController {

    private final TodoService todoService;

    /**
     * 保存当前登录用户待办数据
     */
    @PostMapping("/save")
    @ApiOperation(value = "保存当前登录用户待办数据")
    public BaseResponse<Long> saveTodo(@RequestBody SaveTodoDto todoData) {
        if (!StpUtil.isLogin()) {
            //未登录,直接返回
            return ResultUtils.success(-1L);
        }
       return ResultUtils.success(todoService.saveTodo(todoData.getTodoData()));
    }

    //获取当前登录用户待办数据
    @PostMapping("/get")
    @ApiOperation(value = "获取当前登录用户待办数据")
    public BaseResponse<String> getTodo() {
        if (!StpUtil.isLogin()) {
            //未登录,直接返回
            return ResultUtils.success(null);
        }

        return ResultUtils.success(todoService.getTodoJson());
    }


}
