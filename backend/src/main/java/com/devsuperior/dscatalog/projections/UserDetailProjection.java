package com.devsuperior.dscatalog.projections;

public interface UserDetailProjection {

    String getUsername();
    String getPassword();
    Long getRoleId();
    String getAuthority();
}
