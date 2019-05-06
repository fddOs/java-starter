package ${basePackage}.${businessName}.service.impl;

import ${basePackage}.dao.${modelNameUpperCamel}Mapper;
import ${basePackage}.entity.${modelNameUpperCamel};
import ${basePackage}.${businessName}.service.${modelNameUpperCamel}Service;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

 /**
 * ${swaagerRemark}
 * @author ${author}
 * @date ${date}
 **/
@Service
public class ${modelNameUpperCamel}ServiceImpl implements ${modelNameUpperCamel}Service {
    @Autowired
    private ${modelNameUpperCamel}Mapper ${modelNameLowerCamel}Mapper;


}
