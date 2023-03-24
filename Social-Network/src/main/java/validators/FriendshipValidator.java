package validators;

import domain.friendship.Friendship;

public class FriendshipValidator implements Validator<Friendship>{

    /**
     * validates a friendship
     * @param entity
     * @throws ValidationException if the 2 users from the friendship are the same user
     */
    @Override
    public void validate(Friendship entity) throws ValidationException {
        String errors = new String("");
        if(entity.getUser1().equals(entity.getUser2())){
            throw new ValidationException("user cannot be friend with himself!");
        }
    }
}
