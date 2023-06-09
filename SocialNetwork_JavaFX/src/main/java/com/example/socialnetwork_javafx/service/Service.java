package com.example.socialnetwork_javafx.service;

import com.example.socialnetwork_javafx.domain.Friendship;
import com.example.socialnetwork_javafx.domain.Request;
import com.example.socialnetwork_javafx.domain.User;
import com.example.socialnetwork_javafx.exceptions.RepositoryException;
import com.example.socialnetwork_javafx.exceptions.ValidationException;
import com.example.socialnetwork_javafx.repository.FriendshipDBRepo;
import com.example.socialnetwork_javafx.repository.RequestDBRepo;
import com.example.socialnetwork_javafx.repository.UserDBRepo;
import com.example.socialnetwork_javafx.utils.Constants;
import com.example.socialnetwork_javafx.utils.events.UserEntityChangeEvent;
import com.example.socialnetwork_javafx.utils.observer.Observer;
import com.example.socialnetwork_javafx.utils.observer.Observable;

import java.time.LocalDateTime;
import java.util.*;

public class Service implements Observable<UserEntityChangeEvent> {
    private final UserDBRepo usersRepo;
    private final FriendshipDBRepo friendshipsRepo;
    private final RequestDBRepo requestRepo;
    private List<Observer<UserEntityChangeEvent>> observers=new ArrayList<>();

    private static Service service = null;
    private User user = null;
    public Service(UserDBRepo usersRepo, FriendshipDBRepo friendshipsRepo, RequestDBRepo requestRepo) {
        this.usersRepo = usersRepo;
        this.friendshipsRepo = friendshipsRepo;
        this.requestRepo = requestRepo;
    }

    public synchronized static Service getInstance(UserDBRepo repoUser, FriendshipDBRepo repoFriendship, RequestDBRepo repoRequest) {
        if (service == null)
            service = new Service(repoUser, repoFriendship, repoRequest);
        return service;
    }

    /**
     * Returns list of all users
     * @return List<User>
     */
    public List<User> findAllUsers() {
        return usersRepo.findAll();
    }

    /**
     * Add new user
     * @param username
     * @param password
     * @param email
     * @throws RepositoryException
     * @throws ValidationException
     */
    public void addUser(int id, String username, String password, String email) throws RepositoryException, ValidationException {
        User user = new User(id, username, password, email);
        usersRepo.save(user);
    }

    /**
     * Remove user
     * @param id
     * @throws RepositoryException
     */
    public void removeUser(Integer id) throws RepositoryException, ValidationException {
        if (id == null) {
            throw new ServiceException("ID cannot be null");
        }
        if (usersRepo.find(id) == null) {
            throw new ServiceException("User does not exist");
        }

        User user = usersRepo.find(id);
        List<Friendship> friendships = this.findAllFriendships();
        for (int i = 0; i < friendships.size(); i++) {
            Friendship friendship = friendships.get(i);
            if(Objects.equals(friendship.getId(), id)) {
                friendshipsRepo.delete(friendship);
                i--;
            }
        }
        usersRepo.delete(user);
    }

    /**
     * Return all friendships
     * @return List<Friendship>
     */
    public List<Friendship> findAllFriendships() {
        return friendshipsRepo.findAll();
    }

    /**
     * Add new friendship
     * @param idUser1
     * @param idUser2
     */
    public void addFriendship(int idUser1, int idUser2) {
        int idFriendship = getNewIdFriendship();
        Friendship friendship = new Friendship(idFriendship, idUser1, idUser2, LocalDateTime.now().format(Constants.DATE_TIME_FORMATTER));
        friendshipsRepo.save(friendship);
    }

    /**
     * Updates user
     * @param userUpdate
     * @param userUpdate
     * @throws RepositoryException
     */
    public void updateUser(User userUpdate) throws RepositoryException {
        for(User user : this.findAllUsers()){
            if(Objects.equals(user.getId(), userUpdate.getId())){
                usersRepo.update(userUpdate);
                return;
            }
        }
    }

    /**
     * Remove friendship
     * @param id
     * @throws RepositoryException
     */
    public void removeFriendship(int id) throws RepositoryException {
        Friendship friendship = friendshipsRepo.find(id);
        friendshipsRepo.delete(friendship);
    }

    @Override
    public void addObserver(Observer<UserEntityChangeEvent> e) {
        observers.add(e);

    }

    @Override
    public void removeObserver(Observer<UserEntityChangeEvent> e) {
        observers.remove(e);
    }

    @Override
    public void notifyObservers(UserEntityChangeEvent t) {

        observers.stream().forEach(x->x.update(t));
    }
    public User findOneUser(Integer id) throws RepositoryException {
        return usersRepo.find(id);
    }

    public Iterable<Request> getAllRequests() {
        return requestRepo.findAll();
    }

    public void deleteRequest(Integer id_request) {
        if (id_request == null) {
            throw new ServiceException("ID cannot be null");
        }
        if (findOneRequest(id_request) == null) {
            throw new ServiceException("Friendship does not exist");
        }
        Request request = requestRepo.find(id_request);
        requestRepo.delete(request);
    }

    public void addRequest(String username1, String username2) {
        int id_request = getNewIdRequest();
        String status = "pending..";
        Request request = new Request(id_request, username1, username2, LocalDateTime.now(), status);
        for (Request request1 : requestRepo.findAll()) {
            if (request1.getUsername1().equals(username1) && request1.getUsername2().equals(username2)) {
                throw new ServiceException("Request already sent");
            }
            if (request1.getUsername1().equals(username2) && request1.getUsername2().equals(username1)) {
                throw new ServiceException("Request already received");
            }
        }
        requestRepo.save(request);
    }

    public void updateRequest(Request request) {
        requestRepo.update(request);
    }

    public Request findOneRequest(Integer id) {
        return requestRepo.find(id);
    }

    public Integer getNewIdFriendship() {
        List<Integer> list = new ArrayList<>();
        int id = 0;
        for (Friendship u : friendshipsRepo.findAll())
            list.add(u.getId());
        if(!list.isEmpty()) {
            Collections.sort(list);
            id = list.get(list.size() - 1);
        }
        return id + 1;
    }

    public Integer getNewIdRequest() {

        List<Integer> list = new ArrayList<>();
        int id = 0;
        for (Request u : requestRepo.findAll())
            list.add(u.getId());
        if(!list.isEmpty()) {
            Collections.sort(list);
            id = list.get(list.size() - 1);
        }
        return id + 1;
    }

    public void setUser(User user) {
        this.user = user;
    }
    public User getUser() { return this.user; }
}
