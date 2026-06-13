package structural.proxy.good;

import structural.proxy.model.Account;

// Только загрузка из хранилища — без security и кэша.
// Тестируется в полной изоляции: нет ACL, нет кэша в этом классе.
final class RealAccountService implements AccountService {
    @Override
    public Account get(String id) {
        System.out.println("  [store] loading account " + id);
        return new Account(id, 100_00L);
    }
}
