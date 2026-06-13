package structural.decorator.good;

import structural.decorator.model.Account;

// Единый контракт для реального репозитория и любого декоратора над ним.
interface Repository {
    Account find(String id);
}
