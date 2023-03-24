package repository.file;

import repository.HasID;
import repository.InMemoryRepository;
import validators.RepositoryException;
import validators.ValidationException;
import validators.Validator;

import javax.swing.text.html.parser.Entity;
import java.io.*;
import java.util.Arrays;
import java.util.List;

public abstract class AbstractFileRepository<E extends HasID<ID>,ID> extends InMemoryRepository<E,ID> {
    String filename;
    Validator<E> validator;

    public AbstractFileRepository(String filename, Validator<E> validator) throws RepositoryException, FileNotFoundException {
        this.filename = filename;
        this.validator = validator;
        loadFromFile();
    }

    /**
     * loads the entities from the file in the memory
     * @throws RepositoryException
     */
    private void loadFromFile() throws RepositoryException {
        try(BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while((line=br.readLine())!=null){
                List<String> attr = Arrays.asList(line.split(";"));
                E e = extractEntity(attr);
                try {
                    validator.validate(e);
                    super.save(e);
                } catch (ValidationException ex) {

                }

            }
        }  catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public abstract E extractEntity(List<String> attributes);
    protected abstract String createEntityAsString(E entity);


    @Override
    public E save(E entity) throws RepositoryException {
        E e  = super.save(entity);
        if(e!= null){
            writeToFile(entity);
        }
        return e;
    }

    @Override
    public E delete(ID id){
        E e = super.delete(id);
        writeAllToFile();
        return e;
    }

    @Override
    public E update(E oldEntity, E newEntity) throws RepositoryException {
        E e = super.update(oldEntity, newEntity);
        writeAllToFile();
        return e;
    }

    /**
     * write the entity to the file
     * @param entity written in the file
     */
    protected void writeToFile(E entity){
        try(BufferedWriter bw = new BufferedWriter(new FileWriter(filename,true))) {
            bw.write(createEntityAsString(entity));
            bw.newLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * writes all the entities from the repo in the file
     */
    protected void writeAllToFile(){
        try(BufferedWriter bw = new BufferedWriter(new FileWriter(filename,false))) {
            List<E> entities = findAll();
            for(E entity : entities){
                bw.write(createEntityAsString(entity));
                bw.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } ;
    }


}
