package mega.privacy.android.app.domain.usecase

import mega.privacy.android.app.domain.entity.UserAccount
import mega.privacy.android.app.domain.repository.AccountRepository
import javax.inject.Inject

class DefaultGetAccountDetails @Inject constructor(private val accountsRepository: AccountRepository): GetAccountDetails {
    override fun invoke(): UserAccount {
        return accountsRepository.getUserAccount()
    }
}