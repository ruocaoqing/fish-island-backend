package com.cong.fishisland.service.impl.todo;

import cn.dev33.satoken.stp.StpUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cong.fishisland.model.entity.todo.Todo;
import com.cong.fishisland.service.TodoService;
import com.cong.fishisland.mapper.todo.TodoMapper;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author cong
 * @description 针对表【待办表】的数据库操作Service实现
 * @createDate 2025-03-05 11:28:19
 */
@Service
public class TodoServiceImpl extends ServiceImpl<TodoMapper, Todo>
        implements TodoService {

    @Override
    public Long saveTodo(List<Object> todoData) {
        Todo todo = new Todo();
        todo.setTodoJson(JSON.toJSONString(!todoData.isEmpty() ? todoData : null));
        todo.setUserId(StpUtil.getLoginIdAsLong());
        this.saveOrUpdate(todo, new LambdaQueryWrapper<Todo>().eq(Todo::getUserId, StpUtil.getLoginIdAsLong()));

        return todo.getId();
    }

    @Override
    public String getTodoJson() {
        Todo todo = this.getOne(new LambdaQueryWrapper<Todo>().eq(Todo::getUserId, StpUtil.getLoginIdAsLong()));
        return todo == null ? null : todo.getTodoJson();
    }
}




