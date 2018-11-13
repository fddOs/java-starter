package ${basePackage}.${businessName}.controller;

import ${basePackage}.core.Result;
import ${basePackage}.core.ResultGenerator;
import ${basePackage}.entity.${modelNameUpperCamel};
import ${basePackage}.${businessName}.service.${modelNameUpperCamel}Service;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.web.bind.annotation.*;
import ${basePackage}.core.ResultList;
import javax.annotation.Resource;
import java.util.List;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @description:${swaagerRemark}
 * @author ${author}
 * @time ${date}
 **/
@RestController
//@RequestMapping("${baseRequestMapping}")
@Api(value = "${modelNameUpperCamel}",description = "${swaagerRemark}")
public class ${modelNameUpperCamel}Controller {
    @Autowired
    private ${modelNameUpperCamel}Service ${modelNameLowerCamel}Service;

	@ApiOperation(value = "获取全部${swaagerRemark}")
    @GetMapping("${baseRequestMapping}s")
    public Result<ResultList<${modelNameUpperCamel}>> list(@RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "0") Integer size) {
        PageHelper.startPage(page, size);
        List<${modelNameUpperCamel}> list = ${modelNameLowerCamel}Service.findAll();
        PageInfo pageInfo = new PageInfo(list);
        //return ResultGenerator.genSuccessResult(ResultList.genResultList(pageInfo));
        throw new UnsupportedOperationException();
    }
}
