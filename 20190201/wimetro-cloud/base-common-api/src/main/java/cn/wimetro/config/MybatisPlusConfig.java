package cn.wimetro.config;

import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * MyBatisPlus 分页配置启动项
 * @author wangwei
 * @date  2019/01/08
 * @param
 **/
@Configuration
@MapperScan("cn.wimetro.mapper")
public class MybatisPlusConfig {


    /**
     * 分页配置
     * @author wangwei
     * @date  2019/01/08
     * @return PaginationInterceptor
     **/
    @Bean
    public PaginationInterceptor paginationInterceptor() {
        return new PaginationInterceptor();
    }
}
