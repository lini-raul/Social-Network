package repository;


import validators.RepositoryException;

import java.util.*;

public class InMemoryRepository<E extends HasID<ID>,ID> implements Repository<E,ID> {

    private Map<ID,E> entities;


    public InMemoryRepository(){
        this.entities =  new HashMap<ID,E>();

    }

    /**
     * save an entity in the memory
     * @param entity
     * @return the saved entity
     * @throws IllegalArgumentException if the entity is null
     * @throws RepositoryException if the entity is already saved
     */
    @Override
    public E save(E entity) throws IllegalArgumentException, RepositoryException {
        if(entity==null){
            throw new IllegalArgumentException("Entity cannot be null!");
        }
        if(entities.containsKey(entity.getID())) {
            throw new RepositoryException("Entity already exists!");
        }
        entities.put(entity.getID(), entity);
        return entity;
    }

    /**
     * deletes an entity with a specific id
     * @param id
     * @return the removed entity
     */
    @Override
    public E delete(ID id) {
        return entities.remove(id);
    }

    /**
     *
     * @return the size of the repository
     */
    @Override
    public int size(){
        return entities.size();
    }

    /**
     *
     * @param id
     * @return the entity with a specific id
     * @throws RepositoryException if the entity does not exist in the repository
     */
    @Override
    public E findOne(ID id) throws RepositoryException {
        E entity = entities.get(id);
        if(entity==null)
            throw new RepositoryException("Entity does not exist!");
        return entities.get(id);
    }

    /**
     *
     * @return a list of the entities from the repo
     */
    @Override
    public List<E> findAll()  {
        List<E> lista = new ArrayList<E>();
        for (Map.Entry<ID, E> kv : entities.entrySet()) {
            lista.add(kv.getValue());
        }
        return lista;
    }

    @Override
    public E update(E oldEntity,E newEntity) throws RepositoryException{
        if(newEntity == null)
            throw new IllegalArgumentException("entity must be not null!");

        try{
            findOne(newEntity.getID());
            throw new RepositoryException("Entity already exists!");
        }
        catch(RepositoryException e){
            if(entities.get(oldEntity.getID()) != null) {
                delete(oldEntity.getID());
                save(newEntity);
                return null;
            }
        }
        return newEntity;
    }
}

