package com.ambitious.vcbestm.handler;

import com.ambitious.vcbestm.util.TimeUtils;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

/**
 * 在 插入 / 更新 数据的时候自动填充时间字段
 * @author Ambitious
 * @date 2022/6/13 20:52
 */
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        metaObject.setValue("createTime", TimeUtils.now());
        updateFill(metaObject);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        metaObject.setValue("updateTime", TimeUtils.now());
    }
}
