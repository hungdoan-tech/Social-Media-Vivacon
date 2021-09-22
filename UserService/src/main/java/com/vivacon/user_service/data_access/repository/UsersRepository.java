package com.vivacon.user_service.data_access.repository;

import org.springframework.data.repository.CrudRepository;

import com.vivacon.user_service.data_access.entity.UserEntity;

public interface UsersRepository extends CrudRepository<UserEntity, Long> {

}
