package com.coinomi.wallet.ui;

import android.os.Bundle;

import com.coinomi.core.coins.Value;
import com.coinomi.core.wallet.WalletAccount;
import com.coinomi.wallet.Constants;
import com.coinomi.wallet.R;

import org.bitcoinj.core.Address;

import javax.annotation.Nullable;


public class TradeActivity extends BaseWalletActivity implements
        TradeSelectFragment.Listener, MakeTransactionFragment.Listener, TradeStatusFragment.Listener {

    private int containerRes;

    private enum State {
        INPUT, PREPARATION, SENDING, SENT, FAILED
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trade);

        containerRes = R.id.container;

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(containerRes, new TradeSelectFragment())
                    .commit();
        }
    }

    @Override
    public void onMakeTrade(WalletAccount fromAccount, WalletAccount toAccount, Value amount) {
        Bundle args = new Bundle();
        args.putString(Constants.ARG_ACCOUNT_ID, fromAccount.getId());
        args.putString(Constants.ARG_SEND_TO_ACCOUNT_ID, toAccount.getId());
        if (amount.type.equals(fromAccount.getCoinType())) {
            // TODO set the empty wallet flag in the fragment
            // Decide if emptying wallet or not
            Value lastBalance = fromAccount.getBalance(false);
            if (amount.compareTo(lastBalance) == 0) {
                args.putSerializable(Constants.ARG_EMPTY_WALLET, true);
            } else {
                args.putSerializable(Constants.ARG_SEND_VALUE, amount);
            }
        } else if (amount.type.equals(toAccount.getCoinType())) {
            args.putSerializable(Constants.ARG_SEND_VALUE, amount);
        } else {
            throw new IllegalStateException("Amount does not have the expected type: " + amount.type);
        }

        replaceFragment(MakeTransactionFragment.newInstance(args), containerRes);
    }

    @Override
    public void onAbort() {
        finish();
    }

    @Override
    public void onSignResult(@Nullable Exception error) {
        if (error != null) {
            getSupportFragmentManager().popBackStack();
            DialogBuilder builder = DialogBuilder.warn(this, R.string.trade_error);
            builder.setMessage(getString(R.string.trade_error_sign_tx_message, error.getMessage()));
            builder.setPositiveButton(R.string.button_ok, null)
            .create().show();
        }
    }

    @Override
    public void onTradeDeposit(Address deposit) {
        getSupportFragmentManager().popBackStack();
        replaceFragment(TradeStatusFragment.newInstance(deposit), containerRes);
    }

    @Override
    public void onFinish() {
        finish();
    }
}
