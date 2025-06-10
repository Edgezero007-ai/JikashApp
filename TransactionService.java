import com.gcash.dao.TransactionDAO;
import com.gcash.model.Transaction;
import java.util.List;

public class TransactionService {
    private final TransactionDAO transactionDAO = new TransactionDAO();

    public List<Transaction> getTransactions(int userId) {
        return transactionDAO.getTransactions(userId);
    }
}