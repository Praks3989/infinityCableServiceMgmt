package com.infinityCableService.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import com.infinityCableService.dbUtil.HibernateUtil;
import com.infinityCableService.model.User;

public class UserDao {

	public static void main(String[] arg) {

	}

	// method for inserting a user record in User table
	public static Long createUser(String firstName, String lastName, String emailId, long phoneNumber, String address1,
			String address2, String city, String state, int pinCode, String password, String roleId, String status,
			String userCreateDate) {
		SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
		Session session = sessionFactory.openSession();
		Transaction transaction = null;
		Long userId = null;
		try {
			transaction = session.beginTransaction();
			User user = new User();
			user.setFirstName(firstName);
			user.setLastName(lastName);
			user.setEmailAddress(emailId);
			user.setPhoneNumber(phoneNumber);
			user.setAddress1(address1);
			user.setAddress2(address2);
			user.setPinCode(pinCode);
			user.setCity(city);
			user.setState(state);
			user.setUserCreateDate(userCreateDate);
			user.setPassword(password);
			user.setRoleId(roleId);
			user.setStatus(status);
			session.save(user);
			userId = (Long) session.save(user);
			System.out.println("User saved in DB");

			transaction.commit();

			System.out.println("User insert is committed");
		} catch (HibernateException exception) {
			if (transaction != null)
				transaction.rollback();
			exception.printStackTrace();
		} finally {
			session.close();
		}

		return userId;
	}

	// method for login validation of user using email address and password.
	public static User getUserBasedOnEmailAndPswd(String emailAddress, String password) {
		SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
		Session session = sessionFactory.openSession();
		Transaction transaction = null;

		User user = new User();

		try {
			transaction = session.beginTransaction();
			String hql = " FROM User u WHERE u.emailAddress = :emailId AND u.password = :pswrd";
			Query query = session.createQuery(hql);
			query.setParameter("emailId", emailAddress);
			query.setParameter("pswrd", password);
			List<User> resultList = query.list();

			if (resultList.isEmpty()) {
				System.out.println("There is no User registered with email address = " + emailAddress);
				user = null;
			} else {
				System.out.println(
						"User exists for emailAddress: " + emailAddress + " First Name: " + user.getFirstName());
				user = resultList.get(0);
			}

		} catch (HibernateException exception) {
			if (transaction != null)
				transaction.rollback();
			exception.printStackTrace();
		} finally {
			session.close();
		}

		return user;
	}

	public static User verifyUserBasedOnEmail(String emailAddress) {

		SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
		Session session = sessionFactory.openSession();
		Transaction transaction = null;

		User user = new User();

		try {
			transaction = session.beginTransaction();
			String hql = " FROM User u WHERE u.emailAddress = :emailId";
			Query query = session.createQuery(hql);
			query.setParameter("emailId", emailAddress);
			List<User> resultList = query.list();

			if (resultList.isEmpty()) {
				System.out.println("There is no User registered with email address = " + emailAddress);
				user = null;
			} else {
				System.out.println(
						"User exists for emailAddress: " + emailAddress + " First Name: " + user.getFirstName());
				user = resultList.get(0);
			}
		} catch (HibernateException exception) {
			if (transaction != null)
				transaction.rollback();
			exception.printStackTrace();
		} finally {
			session.close();
		}

		return user;

	}

	public static int resetPaswd(String newPaswrd, String email) {
		SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
		Session session = sessionFactory.openSession();
		Transaction transaction = null;
		int updatedRow = 0;
		try {
			transaction = session.beginTransaction();
			String hql = " UPDATE User u SET u.password = :paswd WHERE u.emailAddress = :emailId";
			Query query = session.createQuery(hql);
			query.setParameter("emailId", email);
			query.setParameter("paswd", newPaswrd);
			updatedRow = query.executeUpdate();
			transaction.commit();
		} catch (HibernateException exception) {
			if (transaction != null)
				transaction.rollback();
			exception.printStackTrace();
		} finally {
			session.close();
		}
		return updatedRow;
	}

	public static int updateUser(User user) {
		SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
		Session session = sessionFactory.openSession();
		Transaction transaction = null;
		int updatedRow = 0;
		User tempUser = new User();
		try {
			transaction = session.beginTransaction();
			Query q = session.createQuery("from User where userId = :userId ");
			q.setParameter("userId", user.getUserId());
			List<User> tempUserList = q.list();
			if (tempUserList != null) {
				tempUser = tempUserList.get(0);
				tempUser.setFirstName(user.getFirstName());
				tempUser.setLastName(user.getLastName());
				tempUser.setEmailAddress(user.getEmailAddress());
				tempUser.setPhoneNumber(user.getPhoneNumber());
				tempUser.setAddress1(user.getAddress1());
				tempUser.setAddress2(user.getAddress2());
				tempUser.setCity(user.getCity());
				tempUser.setState(user.getState());
				tempUser.setPinCode(user.getPinCode());
				tempUser.setPassword(user.getPassword());
				// tempUser.setUserId(userCurrObj.getUserId());

				session.update(tempUser);
			}

			transaction.commit();
		} catch (HibernateException exception) {
			if (transaction != null)
				transaction.rollback();
			exception.printStackTrace();
		} finally {
			session.close();
		}
		return updatedRow;
	}
	
	public static Map<String, Integer> getRegCount(String startDate, String endDate){
		Map<String, Integer> resultMap = new HashMap<String, Integer>();
		
		SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
		Session session = sessionFactory.openSession();
		Transaction transaction = null;
		try {
			transaction = session.beginTransaction();
			String hql = " SELECT COUNT(u.userId) FROM User u WHERE u.roleId = 'Customer' AND u.status = 'Active' AND  (u.userCreateDate BETWEEN :startDt AND :endDt)";
			Query query = session.createQuery(hql);
			query.setParameter("startDt", startDate);
			query.setParameter("endDt", endDate);
			
			int ActiveCount = ((Long)query.uniqueResult()).intValue();
			//updatedRow = query.executeUpdate();
			//transaction.commit();
			resultMap.put("Active", ActiveCount);
			
			String hql2 = " SELECT COUNT(u.userId) FROM User u WHERE u.roleId = 'Customer' AND u.status = 'Inactive' AND  (u.userCreateDate BETWEEN :startDt AND :endDt)";
			Query query2 = session.createQuery(hql2);
			query2.setParameter("startDt", startDate);
			query2.setParameter("endDt", endDate);
			int InactiveCount = ((Long)query2.uniqueResult()).intValue();
			resultMap.put("Inactive", InactiveCount);
			
		} catch (HibernateException exception) {
			if (transaction != null)
				transaction.rollback();
			exception.printStackTrace();
		} finally {
			session.close();
		}
		return resultMap;
	}
	
}
