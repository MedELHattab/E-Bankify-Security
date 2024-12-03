package org.example.ebankifysecurity.service;

import org.example.ebankifysecurity.dto.UserDTO;
import org.example.ebankifysecurity.model.User;
import org.example.ebankifysecurity.repository.UserManagementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    @Autowired
    private UserManagementRepository userRepository;

    @Override
    public UserDTO createUser(UserDTO userDTO) {
        if (userRepository.existsByEmail(userDTO.getEmail())) {
            throw new IllegalArgumentException("Email is already in use.");
        }
        User user = userDTO.toEntity();
        user.setPassword((userDTO.getPassword()));

        User savedUser = userRepository.save(user);
        return UserDTO.fromEntity(savedUser);
    }

    @Override
    public UserDTO getUserById(Long id) {
        User user = userRepository.findById(String.valueOf(id))
                .orElseThrow(() -> new RuntimeException("User not found with id " + id));
        return UserDTO.fromEntity(user);
    }

    @Override
    public UserDTO updateUser(Long id, UserDTO userDTO) {
        User user = userRepository.findById(String.valueOf(id))
                .orElseThrow(() -> new RuntimeException("User not found with id " + id));

        if (!user.getEmail().equals(userDTO.getEmail()) && userRepository.existsByEmail(userDTO.getEmail())) {
            throw new IllegalArgumentException("Email is already in use.");
        }

        user.setName(userDTO.getName());
        user.setEmail(userDTO.getEmail());
        user.setAge(userDTO.getAge());
        user.setMonthlyIncome(userDTO.getMonthlyIncome());
        user.setCreditScore(userDTO.getCreditScore());
        user.setRole(userDTO.getRole());

        User updatedUser = userRepository.save(user);
        return UserDTO.fromEntity(updatedUser);
    }

    @Override
    public void deleteUser(Long id) {
        if (!userRepository.existsById(String.valueOf(id))) {
            throw new RuntimeException("User not found with id " + id);
        }
        userRepository.deleteById(String.valueOf(id));
    }

    @Override
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(UserDTO::fromEntity)
                .collect(Collectors.toList());
    }
}
