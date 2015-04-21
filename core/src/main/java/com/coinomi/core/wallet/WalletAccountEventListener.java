package com.coinomi.core.wallet;

import com.coinomi.core.coins.Value;

import org.bitcoinj.core.Transaction;

/**
 * @author John L. Jegutanis
 */
public interface WalletAccountEventListener {

    void onNewBalance(Value newBalance, Value pendingAmount);

    void onNewBlock(WalletAccount pocket);

    void onTransactionConfidenceChanged(WalletAccount pocket, Transaction tx);

    void onTransactionBroadcastFailure(WalletAccount pocket, Transaction tx);

    void onTransactionBroadcastSuccess(WalletAccount pocket, Transaction tx);

    void onPocketChanged(final WalletAccount pocket);

    void onConnectivityStatus(final WalletPocketConnectivity pocketConnectivity);
}
