// EawaseGame.java
// MoleMain2を参考に作りました
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Objects;
import java.util.Random;

import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class EawaseGame extends JFrame {
	Random rand; // 乱数を発生するクラス
    long starttime=System.currentTimeMillis();								//時間の取得
	float time=(float)((System.currentTimeMillis()-starttime)/1000.0);

    static final int N_TATE = 5; 			// 縦分割数(定数) 5
    static final int N_YOKO = 5;			// 横 5
    int nokori=N_TATE*N_YOKO;				// 残りの枚数
    Card c[] = new Card[N_TATE * N_YOKO];	//カードの配置 、 表示

    int fl=0,tr=0,mekuri=0,point=0,kansei=0,miss=0;							//スコア関係
	Object hitotume;														//１つ目にめくったやつのgetSource()を格納する
	String one=null, two=null;												//押したやつ１つめ２つめのgetText()格納する（mekuri％２で判定）

    // ------------------------------------------------------------------------------------------------------------------------
    // 最初のメッセージ
	JLabel lbl=new JLabel("　　　　     　    ☆ 一枚目をめくってスタート");

    // ------------------------------------------------------------------------------------------------------------------------
    // 諸々設定
    public static void main(String[] args) {
        EawaseGame w = new EawaseGame();
		w.setTitle("数字合わせゲーム！");
        w.setSize(600, 800); //Window のサイズをセット
        w.setVisible(true); //表示する
    }
    // ------------------------------------------------------------------------------------------------------------------------

    // ------------------------------------------------------------------------------------------------------------------------
    // カードの配置と数字の決定
    public EawaseGame () {
    	int fl;												//数字決めるときに使う
        int nakami[] =new int[N_TATE*N_YOKO];				//カードの裏の数字
        for(int x=0;x<N_TATE*N_YOKO;x++) {					//カードの裏の数字を初期化
        	nakami[x]=-1;									//まだわりふられてないものは-1
        }

    	add(lbl,BorderLayout.NORTH);						//ラベル配置
    	JPanel p=new JPanel();
    	p.setLayout(new GridLayout(N_TATE, N_YOKO));

        // -- 格納する数字を決める---------------------------------------------------------------------------------------------
        for (int i = 0; i < N_TATE * N_YOKO; i++) {
            do {
            	fl=0;											//決めるごとにフラグ初期化
          	    try {
                     Thread.sleep(1);
                } catch (InterruptedException e) {
                     e.printStackTrace();
                }
          	    rand = new Random(System.currentTimeMillis()); // 現在時刻で乱数を初期化する
          	    nakami[i] = rand.nextInt(13)+1	;				// まず(1-13)のランダムを格納

            	for(int x=0;x<N_TATE*N_YOKO;x++) {    			//すでに２つ同じ数字が格納されてたらフラグを建てる、[x]を0～24にして一つずつ走査
            		if(nakami[x]==nakami[i]) {					//nakami[i]が[x]と一致したらflを＋１、すでに２つある、fl=2の場合乱数格納をやり直す
            			fl++;
            		}
            	}
            } while (fl>2);	//すでに２つ数字があるとき（フラグが立ってたら）やり直す
        // --------------------------------------------------------------------------------------------------------------------
            c[i] = new Card(""+nakami[i]);					// ボタンi生成
            p.add(c[i]); 									// 配置
        }
        add(p,BorderLayout.CENTER);
    }
    // ------------------------------------------------------------------------------------------------------------------------

    // ------------------------------------------------------------------------------------------------------------------------
    // クリック時の動作
    public class Card extends JButton implements ActionListener {
        public Card(String string) {
            super(string);
            addActionListener(this);
        	setBackground(Color.orange); setForeground(Color.orange);
        }
        public void actionPerformed(ActionEvent ae) {
        	mekuri++;													//めくるたびmekuri++する
        	setBackground(Color.white);setForeground(Color.black);		//めくったときの色（白地に黒）

            // -- 1枚目めくったとき（mekuri%2が奇数）--------------------------------------------------------------------------
        	if(mekuri%2==1) {
        		one=getText();											//押した１つめの数字を格納
                hitotume=ae.getSource();								//場所情報を格納
        		lbl.setText("　　　　('-') 　 ★ 二枚目をめくってください (完成数"+kansei+"/12)");
        			//lbl.setText("選択した数字：+one+"/"+two+"、めくった回数："+mekuri+"、場所："+hitotume+");	//テスト用
        	}

            // -- 2枚目めくったとき（mekuri%2が偶数）→一致不一致の判定--------------------------------------------------------
        	if(mekuri%2==0) {
        		two=getText();											//押した１つめの数字を格納
        			//lbl.setText("選択した数字：+one+"/"+two+"、めくった回数："+mekuri+"、場所："+hitotume+");	//テスト用

	        	// -- カードが一致したとき ------------------------------------------------------------------------------------
	        	if(Objects.equals(one,two)) {
	        		point+=50;kansei++;
	        		lbl.setText("一致！！(^-^)v　 ☆ 一枚目をめくってください (完成数"+kansei+"/12)");
			    	((AbstractButton) ae.getSource()).setBackground(Color.green);		//ボタンの背景を黒く
			    	((AbstractButton) hitotume).setBackground(Color.green);
			    	((AbstractButton) ae.getSource()).setEnabled(false);				//ボタンを無効化
			    	((AbstractButton) hitotume).setEnabled(false);
	        	}
	        	// -- カードが一致しないとき ----------------------------------------------------------------------------------
	        	else{
	        		point-=10;miss++;													//すぐ裏返すと覚えられないから少し待つ
	          	    lbl.setText("不一致！(>-<)  　☆ 一枚目をめくってください (完成数"+kansei+"/12)");
	          	    try {
	                    Thread.sleep(800);
	                } catch (InterruptedException e) {
	                    e.printStackTrace(); }

	          	    setBackground(Color.orange);setForeground(Color.orange);			//ボタンの背景と文字を戻す
			    	((AbstractButton) hitotume).setBackground(Color.orange);
			    	((AbstractButton) hitotume).setForeground(Color.orange);
			    	one=null; 	two=null;		//一応？
	        	}
        	}
        	// ----------------------------------------------------------------------------------------------------------------
            /// 全部完成したらスコア表示
        	if(kansei>=12) {
        		float time=(float)((System.currentTimeMillis()-starttime)/1000.0);
        		lbl.setText("クリア！(^-^)vv(^-^) [スコア "+point+"] めくった回数:"+mekuri+"回 ,完成数:"+kansei+"/12 ,失敗:"+miss+"回 ,かかった時間"+time+"s");				//
        	}
       	}
    }
    // ------------------------------------------------------------------------------------------------------------------------

	public Object getSource() {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}
}