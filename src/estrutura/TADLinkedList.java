package estrutura;
  
public interface TADLinkedList <T> {
    int getSize () ;
    boolean isEmpty () ;
    boolean addFirst ( T element ) ;
    boolean addLast ( T element ) ;
    boolean remove (T element) ;
}

