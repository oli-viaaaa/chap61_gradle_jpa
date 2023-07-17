package com.javalab.board.repository;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.log;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Commit;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.javalab.board.entity.Dept;
import com.javalab.board.entity.User;

import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;

/**
 * @AutoConfigureTestDatabase : 'replace =
 *                            AutoConfigureTestDatabase.Replace.NONE'을 사용하면 데이터
 *                            소스에 대해 MariaDB에 대해 구성된 기존 구성을 사용하도록 Spring Boot에
 *                            지시함. 이를 통해 테스트에서 기본으로 사용하는 임베디드 데이터베이스 대신
 *                            설정파일에(application.properties)의 실제 MariaDB 데이터베이스 를
 *                            사용하라는 의미임.
 */
@ExtendWith(MockitoExtension.class)
@ExtendWith(SpringExtension.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DataJpaTest
@Slf4j
public class UserRepositoryTest {

	@Autowired
	private EmployeeRepository UserRepository;

	@Autowired
	private DeptRepository deptRepository;

	// 부서 더미 데이터 저장
	@Test
	@Disabled
	@Commit // 커밋을 해줘야 데이터베이스에 저장됨, 없으면 롤백(기본 롤백)
	public void testDeptSave() {
		IntStream.rangeClosed(1, 100).forEach(i -> {
			Dept dept = new Dept();
			dept.setDeptId("d00" + i);
			dept.setDeptName("부서" + i);
			deptRepository.save(dept);
		});
		log.info("부서 저장 완료!");
	}

	// 사원 더미 데이터 저장
	@Test
	@Disabled
	@Commit // 커밋을 해줘야 데이터베이스에 저장됨, 없으면 롤백(기본 롤백)
	public void testUserSave() {
		IntStream.rangeClosed(1, 100).forEach(i -> {
			User user = new User();
			user.setName("홍길동" + i);
			user.setAge(21);
			user.setCreateDate(LocalDateTime.now());
			user.setDept(new Dept("d00" + i, "부서" + i, null));
			UserRepository.save(user);
		});
		log.info("사용자 저장 완료!");
	}

	// 사원 더미 데이터 저장
	@Test
	@Disabled
	@Commit // 커밋을 해줘야 데이터베이스에 저장됨, 없으면 롤백(기본 롤백)
	public void testUserUpdate() {
		// 실제 데이터베이스 확인 후 있는 사람의 ID일 것.
		User user = new User();
		user.setId(22);
		user.setName("홍길순");
		user.setAge(15);
		user.setCreateDate(LocalDateTime.now());
		user.setDept(new Dept("d002", "부서2", null));
		UserRepository.save(user);
		log.info("수정 완료!");
	}

	// 사용자 목록 조회
	@Test
	//@Disabled
	@Commit // 커밋을 해줘야 데이터베이스에 저장됨, 없으면 롤백(기본 롤백)
	public void testUserList() {
		List<User> userList = UserRepository.findAll();

		for (User user : userList) {
			log.info("사용자 목록" + user.toString() + "부서명 : " + user.getDept().getDeptName());
		}
		log.info("사용자 목록 조회 완료!");
	}
	
	// 사용자 한명 조회 - 사용자ID로 조회
	@Test
	@Disabled
	@Commit // 커밋을 해줘야 데이터베이스에 저장됨, 없으면 롤백(기본 롤백)
	public void testUserGetById() {
		Optional<User> optUser = UserRepository.findById(100); // 데이터 베이스 확인
		User user = optUser.get();
		log.info("사용자 한명 조회 완료 : " +user.toString());
	}

	// 사용자 한명 조회 - 사용자 이름으로 조회
	// 결과가 여러명일 수 있음
	@Test
	@Disabled
	@Commit
	public void testUserGetByName() {
		List<User> userList = UserRepository.findUserByNameContains("홍길");
		
		for (User user : userList) {
			log.info("사용자 이름으로 조회(여러명) : " +user.toString());
		}
	}
	
	// 사용자 한명 조회 - 사용자 이름으로 조회
	// 결과가 여러명일 수 있음
	@Test
	@Disabled
	@Commit
	public void testUserGetByNameJpql() {
		List<User> userList = UserRepository.findUserByNameJpql("홍길");
		log.info("사용자 이름으로 조회(Jqpl) : ");
		for (User user : userList) {
			log.info("사용자 이름으로 조회(Jqpl) : " +user.toString());
		}
	}
	
	/*
	 * [조회+페이징+정렬]
	 * Spring Data Jpa의 페이징, 정렬은 findAll() 메소드 사용
	 * 	- findAll(Pageable) : PagingAndSortRepository 소속 메소드
	 * 	- Page<T> : 반환타입이 Page<T> 타입인 경우에는 인자가 반드시 pageable이어야 함
	 * 	- Pageable : 페이징 관련 정보를 담고 잇는 객체가 구현해야 할 인터페이스(어떻게 페이징과 정렬을 해야하는지에 대한 정보가 있다.)
	 * 	- PageRequest : Pageable 인터페이스를 구현한 구현(체)
	 * 	- static of() : PageRequest 객체를 얻기 위해서 필요한 정적 메소드
	 */
	
	@Test
	@Disabled
	@Commit
	public void testPagingSorting() {
		Pageable pageable = PageRequest.of(0,10,Sort.by("id").descending());
		Page<User> result = UserRepository.findAll(pageable);
		
		result.stream().forEach(u->System.out.println(u.toString()));
	}
	
	@Test
	//@Disabled
	@Commit
	public void testUserUpdateJpql() {
		// 실제 데이터베이스 확인 후 있는 사람의 ID일 것.
		User user = new User();
		user.setId(25);
		user.setName("홍길자"); // 홍길동 -> 홍길자
		user.setAge(35);
		user.setCreateDate(LocalDateTime.now());
		user.setDept(new Dept("d003", "부서3", null));
		UserRepository.updateUserByParam(user);
		
		log.info("수정 완료!");
	}
	
	/*
	 * @Mock private EmployeeRepository employeeRepository;
	 * 
	 * @Test //@Disabled public void testFindAllEmployees() { // given : 레파지토리 동작을
	 * 모의하기 위한 준비작업(시나리오 List<User> mockUsers = new ArrayList<>(); mockUsers.add(new
	 * User(1, "John", 25, LocalDateTime.now(), null)); mockUsers.add(new User(2,
	 * "Jane", 30, LocalDateTime.now(), null));
	 * 
	 * when(employeeRepository.findAll()).thenReturn(mockUsers);
	 * 
	 * // 레파지토리 메소드 호출 List<User> employees = employeeRepository.findAll();
	 * 
	 * // 결과 검증 assertThat(employees).hasSize(2);
	 * assertThat(employees.get(0).getName()).isEqualTo("John");
	 * assertThat(employees.get(1).getName()).isEqualTo("Jane"); }
	 */
}