package com.offcn.user.service;

import com.offcn.user.pojo.TMember;

public interface UserService {

    public void registerUser(TMember member);

    public TMember login(String username,String password);
}
