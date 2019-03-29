package ${basePackage}.${businessName}.controller;

import cn.ehai.common.core.Result;
import cn.ehai.common.core.ResultList;
import cn.ehai.common.core.ResultGenerator;
import ${basePackage}.entity.${modelNameUpperCamel};
import ${basePackage}.${businessName}.service.${modelNameUpperCamel}Service;
import org.springframework.web.bind.annotation.*;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * ${swaagerRemark}
 * @author ${author}
 * @date ${date}
 */
@RestController
@Api(value = "${modelNameUpperCamel}",description = "${swaagerRemark}")
public class ${modelNameUpperCamel}Controller {
    @Autowired
    private ${modelNameUpperCamel}Service ${modelNameLowerCamel}Service;
  /**
   * //分页查模版
   * @ApiOperation(value = "获取全部${swaagerRemark}")
   * @GetMapping("${baseRequestMapping}s")
   * public Result<ResultList<${modelNameUpperCamel}>> list(@RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "0") Integer size) {
   *     PageHelper.startPage(page, size);
   *     List<${modelNameUpperCamel}> list = ${modelNameLowerCamel}Service.findAll();
   *     PageInfo pageInfo = new PageInfo(list);
   *     return ResultGenerator.genSuccessResult(ResultList.genResultList(pageInfo));
   * }
   */
}
