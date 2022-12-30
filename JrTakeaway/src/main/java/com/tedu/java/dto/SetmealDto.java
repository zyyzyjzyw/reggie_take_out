package com.tedu.java.dto;


import com.tedu.java.pojo.Setmeal;
import com.tedu.java.pojo.SetmealDish;
import lombok.Data;

import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
