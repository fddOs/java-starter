package ${basePackage}.${businessName}.service.impl;

import ${basePackage}.dao.${modelNameUpperCamel}Mapper;
import ${basePackage}.entity.${modelNameUpperCamel};
import ${basePackage}.${businessName}.service.${modelNameUpperCamel}Service;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;


 /**
 * @description:${modelNameUpperCamel}
 * @author ${author}
 * @time ${date}
 **/
@Service
public class ${modelNameUpperCamel}ServiceImpl implements ${modelNameUpperCamel}Service {
    @Autowired
    private ${modelNameUpperCamel}Mapper ${modelNameLowerCamel}Mapper;
    
    /**
     *	持久化
     */
     @Override
    public void save(${modelNameUpperCamel} model){
    		${modelNameLowerCamel}Mapper.insert(model);
    		throw new UnsupportedOperationException();
    }
    
    //更新
    @Override
    public void update(${modelNameUpperCamel} model){
    		${modelNameLowerCamel}Mapper.updateByPrimaryKey(model);
    		throw new UnsupportedOperationException();
    }
    
    //通过ID查找
    @Override
    public ${modelNameUpperCamel} findById(Integer id){
   	 	//return ${modelNameLowerCamel}Mapper.selectByPrimaryKey(id);
    		throw new UnsupportedOperationException();
    }
     
    //获取所有
    @Override
    public List<${modelNameUpperCamel}> findAll(){
    	//	return ${modelNameLowerCamel}Mapper.selectAll();
   	 	throw new UnsupportedOperationException();
    }

}
