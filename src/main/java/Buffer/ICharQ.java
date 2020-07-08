package Buffer;

public interface ICharQ {        //интерфейс очереди
    void put(char ch) throws InterruptedException; // кладем символы в очередь
    char get() throws InterruptedException; //извлекаем символы из очереди
    char end() throws InterruptedException;
}