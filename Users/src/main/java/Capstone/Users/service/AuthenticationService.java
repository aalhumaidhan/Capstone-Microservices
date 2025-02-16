package Capstone.Users.service;

import Capstone.Users.bo.Login.Request.LoginRequest;
import Capstone.Users.bo.Register.Request.*;
import Capstone.Users.entity.*;
import Capstone.Users.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {
    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;

    private final JwtService jwtService;

    public AuthenticationService(UserRepository userRepository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    public UserEntity Register(RegisterRequest request, String Type){
        if (Type.equals("RegisterPersonalRequest")){
            RegisterPersonalRequest personalRequest = (RegisterPersonalRequest) request;
            PersonalEntity user = new PersonalEntity();
            user.setUsername(personalRequest.getUsername());
            user.setEmail(personalRequest.getEmail());
            user.setPassword(passwordEncoder.encode(personalRequest.getPassword()));
            user.setAddress(personalRequest.getAddress());
            user.setFullName(personalRequest.getFullName());
            user.setPhoneNumber(personalRequest.getPhoneNumber());
            user.setCivilId(personalRequest.getCivilId());
            user.setBankAccountNumber(personalRequest.getBankAccount());
            user.setFaceID(personalRequest.getFaceId());
            user.setWalletBalance(personalRequest.getWalletBalance() != null ? personalRequest.getWalletBalance() : 0.0);
            return userRepository.save(user);

        } else if (Type.equals("RegisterBusinessRequest")) {
            RegisterBusinessRequest businessRequest = (RegisterBusinessRequest) request;
            BusinessEntity user = new BusinessEntity();
            user.setUsername(businessRequest.getUsername());
            user.setEmail(businessRequest.getEmail());
            user.setPassword(passwordEncoder.encode(businessRequest.getPassword()));
            user.setAddress(businessRequest.getAddress());
            user.setBusinessLicenseId(businessRequest.getBusinessId());
            user.setBankAccountNumber(businessRequest.getBankAccount());
            user.setName(businessRequest.getName());
            return userRepository.save(user);

        } else if (Type.equals("RegisterAssociateRequest")) {
            RegisterAssociateRequest associateRequest = (RegisterAssociateRequest) request;
            BusinessEntity business = (BusinessEntity) userRepository.findById(associateRequest.getBusinessId()).get();
            AssociateEntity user = new AssociateEntity();
            user.setUsername(associateRequest.getUsername());
            user.setEmail(associateRequest.getEmail());
            user.setPassword(passwordEncoder.encode(associateRequest.getPassword()));
            user.setName(business.getName());
            user.setFullName(associateRequest.getName());
            user.setPhoneNumber(associateRequest.getPhoneNumber());
            user.setAddress(associateRequest.getAddress());
            user.setBankAccountNumber(business.getBankAccountNumber());
            user.setBusinessLicenseId(business.getBusinessLicenseId());
            user.setBusiness(business);
            user = userRepository.save(user);
            business.addAssociate(user);
            userRepository.save(business);
            return user;
        }
        else {
            return null;
        }
    }

    public UserEntity authenticate(LoginRequest input) {
        UserEntity user = userRepository.findByUsername(input.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (!passwordEncoder.matches(input.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Invalid password");
        }

        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(input.getUsername(), input.getPassword()));
        return user;
    }

    public String generateTokenWithAccountType(UserEntity user) {
        String accountType = user.getClass().getSimpleName();
        return jwtService.generateToken(user, accountType, user.getId());
    }
}

