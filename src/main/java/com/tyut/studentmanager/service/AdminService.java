package com.tyut.studentmanager.service;

import com.tyut.studentmanager.domain.Admin;


public interface AdminService {

    Admin findByAdmin(Admin admin);


    int editPswdByAdmin(Admin admin);
}
