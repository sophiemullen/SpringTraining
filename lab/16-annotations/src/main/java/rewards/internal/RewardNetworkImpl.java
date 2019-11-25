package rewards.internal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;
import rewards.AccountContribution;
import rewards.Dining;
import rewards.RewardConfirmation;
import rewards.RewardNetwork;
import rewards.internal.account.Account;
import rewards.internal.account.AccountRepository;
import rewards.internal.restaurant.Restaurant;
import rewards.internal.restaurant.RestaurantRepository;
import rewards.internal.reward.RewardRepository;

import common.money.MonetaryAmount;

/**
 * Rewards an Account for Dining at a Restaurant.
 * <p>
 * The sole Reward Network implementation. This object is an application-layer service responsible for coordinating with
 * the domain-layer to carry out the process of rewarding benefits to accounts for dining.
 * <p>
 * Said in other words, this class implements the "reward account for dining" use case.
 */

/* TODO-03: Annotate the class with an appropriate stereotype annotation
 * to cause component-scan to detect and load this bean.
 * Configure Dependency Injection for all 3 dependencies.
 * Decide if you should use field level or constructor injection. */

@Component
public class RewardNetworkImpl implements RewardNetwork {

    private AccountRepository accountRepository;
    private RestaurantRepository restaurantRepository;
    private RewardRepository rewardRepository;

    /**
     * Creates a new reward network.
     *
     * @param accountRepository    the repository for loading accounts to reward
     * @param restaurantRepository the repository for loading restaurants that determine how much to reward
     * @param rewardRepository     the repository for recording a record of successful reward transactions
     */

    @Autowired
    public RewardNetworkImpl(AccountRepository accountRepository, RestaurantRepository restaurantRepository,
                             RewardRepository rewardRepository) {
        this.accountRepository = accountRepository;
        this.restaurantRepository = restaurantRepository;
        this.rewardRepository = rewardRepository;
    }

    public RewardConfirmation rewardAccountFor(Dining dining) {
        Account account = accountRepository.findByCreditCard(dining.getCreditCardNumber());
        MonetaryAmount amount = restaurantRepository.findByMerchantNumber(dining.getMerchantNumber())
                .calculateBenefitFor(account, dining);

        accountRepository.updateBeneficiaries(account);

        return rewardRepository.confirmReward(account.makeContribution(amount), dining);
    }
}
