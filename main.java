package dev_btc;

import org.bitcoinj.core.*;
import org.bitcoinj.core.listeners.AbstractWalletEventListener;
import org.bitcoinj.kits.WalletAppKit;
import org.bitcoinj.params.RegTestParams;
import org.bitcoinj.script.Script;
import org.bitcoinj.script.ScriptBuilder;
import org.bitcoinj.script.ScriptOpCodes;
import org.bitcoinj.utils.BriefLogFormatter;
import org.bitcoinj.utils.MonetaryFormat;

import java.io.File;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by badr on 2/2/2016.
 */


public class Token {

    public static void send_token(Address destination,NetworkParameters params,WalletAppKit kit){
        System.out.println(" Give the amount to send :");
        Scanner s=new Scanner(System.in);
        double ti=0;
        ti=s.nextDouble();
        Coin amount = Coin.parseCoin(Double.toString(ti));
        System.out.println("# the amount is to "+ti);

        if(ti==0) {
            try {
                System.out.println("000000000000000000000000000");


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else {
            try {
                System.out.println("*********************Start sending transaction op_return************************************");
                Transaction tx = new Transaction(params);
                tx.addOutput(amount, destination);
                System.out.println(" Give Token pointer :");
                Script script = new ScriptBuilder().op(ScriptOpCodes.OP_RETURN).data("my message".getBytes()).build();
                tx.addOutput(Transaction.MIN_NONDUST_OUTPUT, script);
                System.out.println("after changement"+tx);
                System.out.println("Outputus"+tx.getOutputs());
                 kit.wallet().sendCoins( Wallet.SendRequest.forTx(tx));
                System.out.println("*****************************End transaction op_return****************************");
                      } catch (InsufficientMoneyException e) {
                                        e.printStackTrace();
            }

        }

    }

    public  static String get_token_from_tx(Transaction tx){
        String Txx=tx.toString();
        String token_="null";

        if (Txx.contains("RETURN PUSHDATA")&!tx.isPending())
        {
             String token;
            String mydata = tx.getOutputs().toString(); //you could use out.getScriptPubKey()
            mydata.split("RETURN");
            String[] parti = mydata.split("RETURN PUSHDATA"+"\\((.*?)\\)");
            String parta = parti[0];
            String partb = parti[1];

            Pattern pattern = Pattern.compile("\\[(.*?)\\]");
            Matcher matcher = pattern.matcher(partb);
            if (matcher.find())
            {
                token=matcher.group(0).replace("[", "");
                token=token.replace("]", "");
                System.out.println("received token is" +token);
                //System.out.println(matcher.group(1));
                token_=token;
            }
        }
return token_;
    }


        public static void main(String[] args) {

        System.out.println("*********************Tokenizer******************************************");
        BriefLogFormatter.init();
        final NetworkParameters params = RegTestParams.get();
        final WalletAppKit kit = new WalletAppKit(params, new File("Token"), "2") {
        @Override
            protected void onSetupCompleted() {
           // System.out.println("*/*onSetupCompleted*/*");

            }
        };
        kit.setAutoSave(true);
        kit.connectToLocalHost();
        System.out.println("*/*start async*/*");
        kit.startAsync();
        kit.awaitRunning();
        System.out.println("My Adress is : " + kit.wallet().currentReceiveAddress());
        System.out.println("I have :"+ MonetaryFormat.BTC.noCode().format(kit.wallet().getBalance()).toString()+" Tokens");
        //System.out.println("your Wallet data : \n" + kit.wallet());

        kit.wallet().addEventListener(new AbstractWalletEventListener() {
            @Override
            public void onCoinsReceived(Wallet w, Transaction tx, Coin prevBalance, Coin newBalance) {
                Coin value = tx.getValueSentToMe(w);
                System.out.println("Received transaction for " + value.toFriendlyString());
                System.out.println("Transaction will be forwarded after it confirms.");
                System.out.println("-------------------------Transaction in detail-------------------------------------------------------");
                // System.out.println(tx);
               // System.out.println("-----------------------------------------------------------------------------------------------------");
                System.out.println("transaction hash is : " + tx.getHashAsString());
                System.out.println("token is : " + get_token_from_tx(tx));



                String Txx=tx.toString();
                List<TransactionInput> inputs = tx.getInputs();
                List<TransactionOutput> outputs = tx.getOutputs();

                for(TransactionOutput out : outputs){
                    System.out.println("address output "+out.getAddressFromP2PKHScript(params)); //
                 }

                for(TransactionInput in : inputs){
                    System.out.println("address input "+in.getFromAddress()); //
                }


            }

            @Override
            public void onTransactionConfidenceChanged(Wallet wallet, Transaction tx) {

                System.out.println("token in blockchain: " + get_token_from_tx(tx));
                System.out.println("confidence changed: " + tx.getHashAsString());
                TransactionConfidence confidence = tx.getConfidence();
                System.out.println("new block depth: " + confidence.getDepthInBlocks());
            }

            @Override
            public void onCoinsSent(Wallet wallet, Transaction tx, Coin prevBalance, Coin newBalance) {
                System.out.println("New token sent but witing for confirmation");
            }

            @Override
            public void onReorganize(Wallet wallet) {
                System.out.println("wallet onReorganize");

            }

            @Override
            public void onWalletChanged(Wallet wallet) {
              //  System.out.println("The wallet has changed");
            }

            @Override
            public void onKeysAdded(List<ECKey> keys) {
                System.out.println("new key added");
            }

            @Override
            public void onScriptsChanged(Wallet wallet, List<Script> scripts, boolean isAddingScripts) {
                System.out.println("new script added");
            }
        });


        Address destination = new Address(params,"n3SdQD4rGw3trBArNnRH2CSC7Trcy7wPeM");//badr aacount
        send_token(destination,params,kit);
        System.out.println("************ END *****************************");
        System.out.println("Stopping");
        //System.out.println("Stopped!: " + kit.stopAsync());
        kit.awaitTerminated();

      //  kit.awaitTerminated();
       // System.exit(0);











    }

}
