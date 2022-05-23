package com.tyut.studentmanager.mapper;

import com.tyut.studentmanager.domain.Admin;
import org.springframework.stereotype.Repository;


@Repository
public interface AdminMapper {

    Admin findByAdmin(Admin admin);


    int editPswdByAdmin(Admin admin);
}
