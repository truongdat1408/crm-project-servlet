package service;

import repository.UserRepository;

public class LoginService {
    public boolean checkLogin(String email, String password){
        UserRepository userRepository = new UserRepository();
        int count = userRepository.countUserByEmailNPassword(email,password);

        return count>0;
    }
}
