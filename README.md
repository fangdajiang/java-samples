# java-samples
Obsolete codes for work. 曾经在工作中编写过的代码。（已删除敏感信息）

## xxd-starters 是在新新贷公司做架构工作时编写的代码，代码编写大概于 2017 ~ 2018 完成。
> 我编写了大部分代码。有些代码仍然保留了曾经小伙伴的署名，但由于架构组人员变动，这部分代码的大部分仍然由我编写。
### xxd-starter-core 模块，为所有 java 项目提供基础服务。一旦引入了本包，很多功能开箱即用，不用作任何配置，比如 Swagger，缓存，数据库，日志等。
> 示例：java 项目的 Controller 继承 BaseController 后，只要实现业务方法即可，其他都不用管（目前Spring已提供相关注解可以实现，我当时开发时还没有，于是我实现了一个）。
使用 @ApiVersion 注解实现 controller 的版本控制。

> * 版本控制
> * 幂等
> * RESTful API 的基础功能：一般性的req/res参数如token检查，过滤，拦截，组装，安全验证，Swagger...
> * 统一的日志服务
> * 工具包
> * 自动配置
