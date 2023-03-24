package repository;


import validators.RepositoryException;

import java.util.List;

public interface Repository <E,ID>{
    E save(E entity) throws RepositoryException;
    E delete(ID id);
    int size();
    E findOne(ID id) throws RepositoryException;
    List<E> findAll() ;
    E update(E oldEntity,E newEntity) throws RepositoryException;
}
