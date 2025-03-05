package com.cong.fishisland.model.dto.todo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;
/**
 * @author cong
 */
@Data
public class SaveTodoDto {
    @ApiModelProperty(value = "Todo data", required = true)
    List<Object> todoData;
}
