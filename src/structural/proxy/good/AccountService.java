package structural.proxy.good;

import structural.proxy.model.Account;

// Единый контракт для реального сервиса и прокси над ним.
// Клиент не знает — говорит ли он с реальным сервисом или с прокси.
interface AccountService {
    Account get(String id);
}
