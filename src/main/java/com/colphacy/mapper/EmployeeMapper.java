package com.colphacy.mapper;


import com.colphacy.dto.EmployeeDetailDTO;
import com.colphacy.model.Employee;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface EmployeeMapper {
    @Mapping(source = "employee.role.name", target = "role")
    @Mapping(target = "branch", expression = "java(employee.getBranch() != null ? employee.getBranch().toString() : null)")
    EmployeeDetailDTO employeeToEmployeeDetailDTO(Employee employee);
}
