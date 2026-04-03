package org.faicai.d4c.config;

import org.faicai.d4c.core.SqlExecutionContext;
import org.faicai.d4c.core.SqlExecutionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

// 责任链构建器
@Component
public class SqlExecutionChain {
    private final List<SqlExecutionHandler> handlers;
    
    @Autowired
    public SqlExecutionChain(List<SqlExecutionHandler> handlers) {
        // 根据@Order注解排序
        this.handlers = handlers.stream()
                .sorted(AnnotationAwareOrderComparator.INSTANCE)
                .collect(Collectors.toList());
        
        // 构建链
        for (int i = 0; i < this.handlers.size() - 1; i++) {
            this.handlers.get(i).setNextHandler(this.handlers.get(i + 1));
        }
    }
    
    public void execute(SqlExecutionContext context) {
        if (!handlers.isEmpty()) {
            handlers.get(0).handle(context);
        }
    }
}