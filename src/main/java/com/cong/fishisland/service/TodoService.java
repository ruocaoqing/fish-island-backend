package com.cong.fishisland.service;

import com.cong.fishisland.model.entity.todo.Todo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author cong
* @description 针对表【待办表】的数据库操作Service
* @createDate 2025-03-05 11:28:19
*/
public interface TodoService extends IService<Todo> {

    Long saveTodo(List<Object> todoData);

    String getTodoJson();
}
