package com.coinomi.core.coins;

import org.bitcoinj.core.Address;
import org.bitcoinj.core.AddressFormatException;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.params.Networks;

import com.google.common.collect.ImmutableList;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * @author John L. Jegutanis
 */
public enum CoinID {
    BITCOIN_MAIN(BitcoinMain.get()),
    BITCOIN_TEST(BitcoinTest.get()),
    CRYPTOESCUDO_MAIN(CryptoescudoMain.get()),
    LITECOIN_MAIN(LitecoinMain.get()),
    LITECOIN_TEST(LitecoinTest.get()),
    DOGECOIN_MAIN(DogecoinMain.get()),
    DOGECOIN_TEST(DogecoinTest.get()),
    REDDCOIN_MAIN(ReddcoinMain.get()),
    PEERCOIN_MAIN(PeercoinMain.get()),
    DASH_MAIN(DashMain.get()),
    NUSHARES_MAIN(NuSharesMain.get()),
    NUBITS_MAIN(NuBitsMain.get()),
    NAMECOIN_MAIN(NamecoinMain.get()),
    FEATHERCOIN_MAIN(FeathercoinMain.get()),
    BLACKCOIN_MAIN(BlackcoinMain.get()),
    RUBYCOIN_MAIN(RubycoinMain.get()),
    URO_MAIN(UroMain.get()),
    DIGITALCOIN_MAIN(DigitalcoinMain.get()),
    CANNACOIN_MAIN(CannacoinMain.get()),
    DIGIBYTE_MAIN(DigibyteMain.get()),;

    private static HashMap<String, CoinType> idLookup = new HashMap<>();
    private static HashMap<String, CoinType> symbolLookup = new HashMap<>();

    static {
        Set<NetworkParameters> bitcoinjNetworks = Networks.get();
        for (NetworkParameters network : bitcoinjNetworks) {
            Networks.unregister(network);
        }

        for (CoinID id : values()) {
            Networks.register(id.type);
        }

        for (CoinID id : values()) {
            if (symbolLookup.containsKey(id.type.symbol)) {
                throw new IllegalStateException(
                        "Coin currency codes must be unique, double found: " + id.type.symbol);
            }
            symbolLookup.put(id.type.symbol, id.type);

            if (idLookup.containsKey(id.type.getId())) {
                throw new IllegalStateException(
                        "Coin IDs must be unique, double found: " + id.type.getId());
            }
            idLookup.put(id.type.getId(), id.type);
        }
    }

    private final CoinType type;

    private CoinID(final CoinType type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return type.getId();
    }

    public CoinType getCoinType() {
        return type;
    }

    public static List<CoinType> getSupportedCoins() {
        return ImmutableList.copyOf(idLookup.values());
    }

    public static CoinType typeFromId(String stringId) {
        if (idLookup.containsKey(stringId)) {
            return idLookup.get(stringId);
        } else {
            throw new IllegalArgumentException("Unsupported ID: " + stringId);
        }
    }

    public static CoinID fromUri(String input) {
        for (CoinID id : values()) {
            if (input.startsWith(id.getCoinType().getUriScheme() + "://")) {
                return id;
            } else if (input.startsWith(id.getCoinType().getUriScheme() + ":")) {
                return id;
            }
        }
        throw new IllegalArgumentException("Unsupported URI: " + input);
    }

    public static CoinType typeFromAddress(String address) throws AddressFormatException {
        NetworkParameters addressParams = new Address(null, address).getParameters();
        if (addressParams instanceof CoinType) {
            return (CoinType) addressParams;
        } else {
            throw new AddressFormatException("Unsupported address network: " + addressParams.getId());
        }
    }

    public static boolean isSymbolSupported(String symbol) {
        return symbolLookup.containsKey(symbol);
    }

    public static CoinType typeFromSymbol(String symbol) {
        if (symbolLookup.containsKey(symbol.toUpperCase())) {
            return symbolLookup.get(symbol.toUpperCase());
        } else {
            throw new IllegalArgumentException("Unsupported coin symbol: " + symbol);
        }
    }
}
