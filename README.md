# spring-boot-validation-demo

提到输入参数的基本验证（非空、长度、大小、格式...），在以前我们还是通过手写代码，各种if、else、StringUtils.isEmpty、CollectionUtils.isEmpty...，真感觉快要疯了，太繁琐，Low爆了...，其实在Java生态提供了一套标准[JSR-380（aka. Bean Validation 2.0，part of Jakarta EE and JavaSE）](https://jcp.org/en/jsr/detail?id=380)，它已成为`对象验证`事实上的标准，这套标准可以通过注解的形式（如@NotNull, @Size...）来对bean的属性进行验证。而`Hibernate Validator`对这套标准进行了实现，`SpringBoot Validation`无缝集成了Hibernate Validator、自定义验证器、自动验证的功能。下文将对SpringBoot集成Validation进行展开。

**注：** 完整示例代码可参见GitHub：[https://github.com/marqueeluo/spring-boot-validation-demo](https://github.com/marqueeluo/spring-boot-validation-demo)



# constraints分类
JSR-380的支持的constrants注解汇总如下表：
| 分类 | 注解      | 适用对象 | null是否验证通过 | 说明
|:--------| :-- | :---- | :---- |:----
| 非空 | @NotNull | 所有对象| No | 不是null
| 非空 | @NotEmpty | CharSequence, Collection, Map, Array | No | 不是null、不是""、size>0
| 非空 | @NotBlank | CharSequence | No | 不是null、trim后长度大于0
| 非空 | @Null | 所有对象 | Yes | 是null
| 长度 | @Size(min=0, max=Integer.MAX_VALUE) | CharSequence, Collection, Map, Array | Yes | 字符串长度、集合size
| 大小 | @Positive | BigDecimal, BigInteger, byte, short, int, long, float, double | Yes | 数字>0
| 大小 | @PositiveOrZero | BigDecimal, BigInteger, byte, short, int, long, float, double | Yes | 数字>=0
| 大小 | @Negative | BigDecimal, BigInteger, byte, short, int, long, float, double | Yes | 数字<0
| 大小 | @NegativeOrZero | BigDecimal, BigInteger, byte, short, int, long, float, double | Yes | 数字<=0
| 大小 | @Min(value=0L) | BigDecimal, BigInteger, byte, short, int, long | Yes | 数字>=min.value
| 大小 | @Max(value=0L) | BigDecimal, BigInteger, byte, short, int, long | Yes | 数字<=max.value
| 大小 | @Range(min=0L, max=Long.MAX_VALUE) | BigDecimal, BigInteger, byte, short, int, long | Yes | range.min<=数字<=range.max
| 大小 | @DecimalMin(value="") | BigDecimal, BigInteger, CharSequence, byte, short, int, long | Yes | 数字>=decimalMin.value
| 大小 | @DecimalMax(value="") | BigDecimal, BigInteger, CharSequence, byte, short, int, long | Yes | 数字<=decimalMax.value
| 日期 | @Past | <ul><li>java.util.Date</li><li>java.util.Calendar</li><li>java.time.Instant</li><li>java.time.LocalDate</li><li>java.time.LocalDateTime</li><li>java.time.LocalTime</li><li>java.time.MonthDay</li><li>java.time.OffsetDateTime</li><li>java.time.OffsetTime</li><li>java.time.Year</li><li>java.time.YearMonth</li><li>java.time.ZonedDateTime</li><li>java.time.chrono.HijrahDate</li><li>java.time.chrono.JapaneseDate</li><li>java.time.chrono.MinguoDate</li><li>java.time.chrono.ThaiBuddhistDate</li></ul> | Yes | 时间在当前时间之前
| 日期 | @PastOrPresent | 同上 | Yes | 时间在当前时间之前 或者等于此时
| 日期 | @Future | 同上 | Yes | 时间在当前时间之后
| 日期 | @FutureOrPresent | 同上 | Yes | 时间在当前时间之后 或者等于此时
| 格式 | @Pattern(regexp="", flags={}) | CharSequence | Yes | 匹配正则表达式
| 格式 | @Email<br/>@Email(regexp=".*", flags={})| CharSequence | Yes | 匹配邮箱格式
| 格式 | @Digts(integer=0, fraction=0) | BigDecimal, BigInteger, CharSequence, byte, short, int, long | Yes | 必须是数字类型，且满足整数位数<=digits.integer, 浮点位数<=digits.fraction
| 布尔| @AssertTrue | boolean | Yes | 必须是true
| 布尔| @AssertFalse | boolean | Yes | 必须是false

# 对象集成constraints示例
```java
/**
 * 用户 - DTO
 *
 * @author luohq
 * @date 2021-09-04 13:45
 */
public class UserDto {

    @NotNull(groups = Update.class)
    @Positive
    private Long id;

    @NotBlank
    @Size(max = 32)
    private String name;

    @NotNull
    @Range(min = 1, max = 2)
    private Integer sex;

    @NotBlank
    @Pattern(regexp = "^\\d{8,11}$")
    private String phone;

    @NotNull
    @Email
    private String mail;

    @NotNull
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$")
    private String birthDateStr;

    @NotNull
    @PastOrPresent
    private LocalDate birthLocalDate;

    @NotNull
    @Past
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime registerLocalDatetime;

    @Valid
    @NotEmpty
    private List<OrgDto> orgs;

	//省略getter、setter、toString方法	
}

/**
 * 组织 - DTO
 *
 * @author luohq
 * @date 2021-09-04 14:10
 */
public class OrgDto {
    @NotNull
    @Positive
    private Long orgId;

    @NotBlank
    @Size(min = 1, max = 32)
    private String orgName;
    
    //省略getter、setter、toString方法	
}
```
**注：**
- **可通过constraints注解的`groups`指定分组**
即指定constraints仅在指定group生效，默认均为Default分组，
后续可通过`@Validated({MyGroupInterface.class}）`形式进行分组的指定
- **可通过`@Valid`注解进行级联验证（Cascaded Validation，即嵌套对象验证）**
如上示例中@Valid添加在 List&lt;OrgDto&gt; orgs上，即会对list中的每个OrgDto进行验证



# SpringBoot集成自动验证
参考：
[https://www.baeldung.com/javax-validation-method-constraints#validation](https://www.baeldung.com/javax-validation-method-constraints#validation)
## 集成maven依赖
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-validation</artifactId>
</dependency>
```

## 验证RequestBody、Form对象参数
在参数前加@Validated
![在这里插入图片描述](https://img-blog.csdnimg.cn/a8d81204b9334306be903424da8f4aa3.png?x-oss-process=image/watermark,type_ZHJvaWRzYW5zZmFsbGJhY2s,shadow_50,text_Q1NETiBA572X5bCP54isRVg=,size_20,color_FFFFFF,t_70,g_se,x_16)

## 验证简单参数
在controller类上加@Validated
![在这里插入图片描述](https://img-blog.csdnimg.cn/eb349e8996e94a5fa15858df11e21c87.png?x-oss-process=image/watermark,type_ZHJvaWRzYW5zZmFsbGJhY2s,shadow_50,text_Q1NETiBA572X5bCP54isRVg=,size_20,color_FFFFFF,t_70,g_se,x_16)
## 验证指定分组
![在这里插入图片描述](https://img-blog.csdnimg.cn/ea2b6d809a6e403b9f58a6a6ce22dda0.png?x-oss-process=image/watermark,type_ZHJvaWRzYW5zZmFsbGJhY2s,shadow_50,text_Q1NETiBA572X5bCP54isRVg=,size_20,color_FFFFFF,t_70,g_se,x_16)


# 全局controller验证异常处理
通过@ControllerAdvice、@ExceptionHandler来对SpringBoot Validation验证框架抛出的异常进行统一处理，
并将错误信息拼接后统一返回，具体处理代码如下：
```java
import com.luo.demo.validation.domain.result.CommonResult;
import com.luo.demo.validation.enums.RespCodeEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * controller增强 - 通用异常处理
 *
 * @author luohq
 * @date 2021-09-04 13:43
 */
@ControllerAdvice
public class ControllerAdviceHandler {

    private static final Logger log = LoggerFactory.getLogger(ControllerAdviceHandler.class);

    /**
     * 是否在响应结果中展示验证错误提示信息
     */
    @Value("${spring.validation.msg.enable:true}")
    private Boolean enableValidationMsg;

    /**
     * 符号常量
     */
    private final String DOT = ".";
    private final String SEPARATOR_COMMA = ", ";
    private final String SEPARATOR_COLON = ": ";

    /**
     * 验证异常处理 - 在@RequestBody上添加@Validated处触发
     *
     * @param request
     * @param ex
     * @return
     */
    @ExceptionHandler({MethodArgumentNotValidException.class})
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public CommonResult handleMethodArgumentNotValidException(HttpServletRequest request, MethodArgumentNotValidException ex) {
        log.warn("{} - MethodArgumentNotValidException!", request.getServletPath());
        CommonResult commonResult = CommonResult.respWith(RespCodeEnum.PARAM_INVALID.getCode(), this.convertFiledErrors(ex.getBindingResult().getFieldErrors()));
        log.warn("{} - resp with param invalid: {}", request.getServletPath(), commonResult);
        return commonResult;
    }

    /**
     * 验证异常处理 - form参数（对象参数，没有加@RequestBody）触发
     *
     * @param request
     * @param ex
     * @return
     */
    @ExceptionHandler({BindException.class})
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public CommonResult handleBindException(HttpServletRequest request, BindException ex) {
        log.warn("{} - BindException!", request.getServletPath());
        CommonResult commonResult = CommonResult.respWith(RespCodeEnum.PARAM_INVALID.getCode(), this.convertFiledErrors(ex.getFieldErrors()));
        log.warn("{} - resp with param invalid: {}", request.getServletPath(), commonResult);
        return commonResult;
    }


    /**
     * 验证异常处理 - @Validated加在controller类上，
     * 且在参数列表中直接指定constraints时触发
     *
     * @param request
     * @param ex
     * @return
     */
    @ExceptionHandler({ConstraintViolationException.class})
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public CommonResult handleConstraintViolationException(HttpServletRequest request, ConstraintViolationException ex) {
        log.warn("{} - ConstraintViolationException - {}", request.getServletPath(), ex.getMessage());
        CommonResult commonResult = CommonResult.respWith(RespCodeEnum.PARAM_INVALID.getCode(), this.convertConstraintViolations(ex));
        log.warn("{} - resp with param invalid: {}", request.getServletPath(), commonResult);
        return commonResult;
    }

    /**
     * 全局默认异常处理
     *
     * @param request
     * @param ex
     * @return
     */
    @ExceptionHandler({Throwable.class})
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public CommonResult handleException(HttpServletRequest request, Throwable ex) {
        log.warn("{} - Exception!", request.getServletPath(), ex);
        CommonResult commonResult = CommonResult.failed();
        log.warn("{} - resp failed: {}", request.getServletPath(), commonResult);
        return commonResult;
    }

    /**
     * 转换FieldError列表为错误提示信息
     *
     * @param fieldErrors
     * @return
     */
    private String convertFiledErrors(List<FieldError> fieldErrors) {
        return Optional.ofNullable(fieldErrors)
                .filter(fieldErrorsInner -> this.enableValidationMsg)
                .map(fieldErrorsInner -> fieldErrorsInner.stream()
                        .flatMap(fieldError -> Stream.of(fieldError.getField(), SEPARATOR_COLON, fieldError.getDefaultMessage(), SEPARATOR_COMMA))
                        .collect(Collectors.joining()))
                .map(msg -> msg.substring(0, msg.length() - SEPARATOR_COMMA.length()))
                .orElse(null);
    }

    /**
     * 转换ConstraintViolationException异常为错误提示信息
     *
     * @param constraintViolationException
     * @return
     */
    private String convertConstraintViolations(ConstraintViolationException constraintViolationException) {
        return Optional.ofNullable(constraintViolationException.getConstraintViolations())
                .filter(constraintViolations -> this.enableValidationMsg)
                .map(constraintViolations -> constraintViolations.stream()
                        .flatMap(constraintViolation -> {
                            String path = constraintViolation.getPropertyPath().toString();
                            path = path.substring(path.lastIndexOf(DOT) + 1);
                            String errMsg = constraintViolation.getMessage();
                            return Stream.of(path, SEPARATOR_COLON, errMsg, SEPARATOR_COMMA);
                        }).collect(Collectors.joining())
                ).map(msg -> msg.substring(0, msg.length() - SEPARATOR_COMMA.length()))
                .orElse(null);

    }
}
```
参数验证未通过返回结果示例：
![在这里插入图片描述](https://img-blog.csdnimg.cn/8498cdf2035c4a8da4e665701c624e36.png?x-oss-process=image/watermark,type_ZHJvaWRzYW5zZmFsbGJhY2s,shadow_50,text_Q1NETiBA572X5bCP54isRVg=,size_20,color_FFFFFF,t_70,g_se,x_16)


**注：** 其中CommonResult为统一返回结果，可根据自己业务进行调整
![在这里插入图片描述](https://img-blog.csdnimg.cn/73b92c8f1bfa4546a7f1a7d3538be972.png?x-oss-process=image/watermark,type_ZHJvaWRzYW5zZmFsbGJhY2s,shadow_50,text_Q1NETiBA572X5bCP54isRVg=,size_18,color_FFFFFF,t_70,g_se,x_16)



# 自定义constraints
自定义field constraint注解主要分为以下几步：

（1）定义`constraint annotation注解及其属性`

（2）通过注解的元注解`@Constraint(validatedBy = {})`关联的具体的验证器实现

（3）实现`验证器`逻辑

## @DateFormat
具体字符串日期格式constraint @DateFormat定义示例如下：
```java

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;
import static java.lang.annotation.ElementType.ANNOTATION_TYPE;

/**
 * The annotated {@code CharSequence} must match date format.
 * The default date format is "yyyy-MM-dd".
 * Can override with property "format".
 * see {@link java.time.format.DateTimeFormatter}.
 * <p>
 * Accepts {@code CharSequence}. {@code null} elements are considered valid.
 *
 * @author luo
 * @date 2021-09-05
 */
@Documented
@Constraint(validatedBy = DateFormatValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD, ANNOTATION_TYPE,})
@Retention(RetentionPolicy.RUNTIME)
public @interface DateFormat {
    String message() default "日期格式不正确";

    String format() default "yyyy-MM-dd";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}



import org.springframework.util.StringUtils;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.format.DateTimeFormatter;

/**
 * Date Format validator
 *
 * @author luohq
 * @date 2021-09-05
 */
public class DateFormatValidator implements ConstraintValidator<DateFormat, String> {

    private String format;

    @Override
    public void initialize(DateFormat dateFormat) {
        this.format = dateFormat.format();
    }

    @Override
    public boolean isValid(String dateStr, ConstraintValidatorContext cxt) {
        if (!StringUtils.hasText(dateStr)) {
            return true;
        }
        try {
            DateTimeFormatter.ofPattern(this.format).parse(dateStr);
            return true;
        } catch (Throwable ex) {
            return false;
        }
    }
}
```
## @PhoneNo
在查看hbernate-validator中URL、Email约束实现时，发现可以通过元注解的形式去复用constraint实现（如@Pattern），故参考如上方式实现@PhoneNo约束

```java
import javax.validation.Constraint;
import javax.validation.OverridesAttribute;
import javax.validation.Payload;
import javax.validation.ReportAsSingleViolation;
import javax.validation.constraints.Pattern;
import java.lang.annotation.Documented;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;


/**
 * The annotated {@code CharSequence} must match phone no format.
 * The regular expression follows the Java regular expression conventions
 * see {@link java.util.regex.Pattern}.
 * <p>
 * Accepts {@code CharSequence}. {@code null} elements are considered valid.
 *
 * @author luo
 * @date 2021-09-05
 */
@Documented
@Constraint(validatedBy = {})
@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
@Retention(RUNTIME)
@Repeatable(PhoneNo.List.class)
@ReportAsSingleViolation
@Pattern(regexp = "")
public @interface PhoneNo {
    String message() default "电话号码格式不正确";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    /**
     * @return an additional regular expression the annotated PhoneNo must match. The default is "^\\d{8,11}$"
     */
    @OverridesAttribute(constraint = Pattern.class, name = "regexp") String regexp() default "^\\d{8,11}$";

    /**
     * @return used in combination with {@link #regexp()} in order to specify a regular expression option
     */
    @OverridesAttribute(constraint = Pattern.class, name = "flags") Pattern.Flag[] flags() default {};

    /**
     * Defines several {@code @URL} annotations on the same element.
     */
    @Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
    @Retention(RUNTIME)
    @Documented
    @interface List {
        PhoneNo[] value();
    }
}

```
**注：** 同理可以实现@IdNo约束
## 使用自定义constraints注解

可将之前的对象集成示例中代码调整为使用自定义验证注解如下：

```java
/**
 * 用户 - DTO
 *
 * @author luohq
 * @date 2021-09-04 13:45
 */
public class UserDto {
    ...
    @NotBlank
    //@Pattern(regexp = "^\\d{8,11}$")
    @PhoneNo
    private String phone;
    
    @NotBlank
    @IdNo
    private String idNo;

    @NotNull
    //@Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$")
    @DateFormat
    //@DateTimeFormat
    private String birthDateStr;

    ...
}
```

同时自定义contrain还支持`跨多参数`、`验证对象里的多个field`、`验证返回对象`等用法，待后续再详细探索。

# 问题
通过在对象属性、方法参数上`标注注解`的形式，需要`侵入代码`，之前有的架构师不喜欢这种风格。

在一方开发时，我们有全部源码且在公司内部，这种方式还是可以的，且集成比较方便，

但是依赖三方Api jar包（参数对象定义在jar包中），我们无法直接去修改参数对象，依旧使用这种侵入代码的注解方式就不适用了，

针对三方包、或者替代注解这种形式，之前公司内部有实现过`基于xml配置`的形式进行验证，

这种方式不侵入参数对象，且集成也还算方便，

但是用起来还是没有直接在代码里写注解来的顺手（代码有补全、有提示、程序员友好），

**所以一方开发时，首选推荐SpringBoot Validation这套体系，无法直接编辑参数对象时再考虑其他方式。**


参考：

[【自定义validator - field、class level】https://www.baeldung.com/spring-mvc-custom-validator](https://www.baeldung.com/spring-mvc-custom-validator)

[【Spring boot集成validation、全局异常处理】https://www.baeldung.com/spring-boot-bean-validation](https://www.baeldung.com/spring-boot-bean-validation)

[【JSR380、非Spring框架集成validation】https://www.baeldung.com/javax-validation](https://www.baeldung.com/javax-validation)

[【方法约束 - Single param、Cross param、Return value自定义constraints、编程调用验证】https://www.baeldung.com/javax-validation-method-constraints](https://www.baeldung.com/javax-validation-method-constraints)

[Spring Validation最佳实践及其实现原理，参数校验没那么简单！](https://segmentfault.com/a/1190000023471742)

[https://reflectoring.io/bean-validation-with-spring-boot/](https://reflectoring.io/bean-validation-with-spring-boot/)




