package com.javalab.board.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.javalab.board.entity.User;

/*
 * 레파지토리 인터페이스로 다음 코드로 작성만 해놓으면 내부적으로
 * C/R/U/D 가 자동으로 구현되어진다.
 * JpaRepository<User, Integer> :
 * 	- Dept : 엔티티의 타입(클래스) 이름,
 * 	  String : 키 컬럼의 자료형(wrapper) 타입
 */

public interface EmployeeRepository extends JpaRepository<User, Integer> {

	/*
	 * [개발자 정의 메소드] 
	 * Spring Data JPA에서 기본으로 제공해주는 메소드 외에 필요한 메소드는
	 * 개발자가 직접 만들 수 있다.
	 */
	List<User> findUserByNameContains(String name);
	
	/*
	 * [개발자 정의 메소드] 조회
	 * JPQL(Java Persistence Query Language)
	 * @Query : EntityManager에 질의하는 형식을 취한다.
	 */
	@Transactional
	@Modifying
	@Query("SELECT u FROM User u WHERE u.name like %:name%")
	List<User> findUserByNameJpql(@Param("name") String name); // org.springframework.data.repository.query.Param

	/*
	 * [개발자 정의 메소드] 수정
	 *  - Jpql을 사용하여 업데이터 쿼리 생성
	 *  - @Modifying 어노테이션 : update, delete Query시 사용
	 *  - nativeQuery=false : 이 쿼리는 일반 Native SQL이 아님을 명시
	 */
	
	//	@Modifying
	//	@Query(value="UPDATE User u SET u.name = #{#user.name}, u.age = #{#user.age} WHERE u.id = #{#user.id}", 
	//			nativeQuery=false)
	//	int updateUserByParam(@Param("user") User user);
	
	@Transactional
	@Modifying
	@Query("UPDATE User u SET u.name = :#{#user.name}, u.age = :#{#user.age}, u.dept = :#{#user.dept} WHERE u.id = :#{#user.id}")
	int updateUserByParam(@Param("user") User user);


}