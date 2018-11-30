package jp.ac.asojuku.st.myrollingball

import android.content.Context
import android.content.pm.ActivityInfo
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Color.*
import android.graphics.Paint
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.SurfaceHolder
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(),
        SensorEventListener, SurfaceHolder.Callback {

    // プロパティ
    private var surfaceWidth: Int = 0;   // サーフェスの幅
    private var surfaceHeight: Int = 0;   // サーフェスの高さ

    private val radius = 50.0f;   // ボールの半径
    private val coef = 1000.0f;   // ボールの移動量を計算するための係数（計数）

    private var ballX: Float = 0f;   //ボールの現在のX座標
    private var ballY: Float = 0f;   //ボールの現在のY座標
    private var vx: Float = 0f;   // ボールのX方向の加速度
    private var vy: Float = 0f;   // ボールのY方向の加速度
    private var time: Long = 0L;   // 前回の取得時間

    private var isreturn = false;
    // 誕生時のライフサイクルイベント
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val holder = surfaceView.holder;   // サーフェスホルダーを取得
        holder.addCallback(this);   // サーフェスホルダーのコールバックに自クラスを追加
        // 画面の縦横指定をアプリから指定してロック
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;

        btn_reset.setOnClickListener { OnResetButton(); }
    }

    // 画面表示・再表示のライフサイクルイベント
    override fun onResume() {
        // 親クラスのonResume()処理
        super.onResume()
        // 自クラスのonResume()処理
//        // センサーマネージャーをOSか取得
//        val sensorManager = this.getSystemService(Context.SENSOR_SERVICE)as SensorManager;
//        // 加速度センサー(Accelerometer)を指定してセンサーマネージャーからセンサーを取得
//        val accSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
//        // リスナー登録して加速度センサーの監視を開始
//        sensorManager.registerListener(
//                this,   // イベントリスナー機能を持つインスタンス（自クラスのインスタンス）
//                 accSensor,   // 監視するセンサー（加速度センサー）
//                 SensorManager.SENSOR_DELAY_GAME   // センサーの更新頻度
//        )

    }

    // 画面が非表示の時のイベントコールバック
    override fun onPause() {
        super.onPause()
//        // センサーマネージャーを取得
//        val sensorManager = this.getSystemService(Context.SENSOR_SERVICE)as SensorManager;
//        // センサーマネージャーに登録したリスナーを解除（自分自身を解除）
//        sensorManager.unregisterListener(this);
    }

    // 制度が変わった時のイベントコールバック
    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {

    }

    // センサーの値が変わった時のイベントコールバック
    override fun onSensorChanged(event: SensorEvent?) {
        if (event == null) {
            return; }

        // センサーの値が変わったらログに出力
//        // 加速度センサーか判定
//        if (event.sensor.type == Sensor.TYPE_ACCELEROMETER){
//            // ログ出力用文字列を組み立て
//            val str:String = "x = ${event.values[0].toString()}" +
//                    ", y = ${event.values[1].toString()}" +
//                    " ,z = ${event.values[2].toString()}";
//            // デバッグログに出力
//            // Log.d("加速度センサー",str);
//            // テキストビューに表示
//            txvMain.text = str;
//        }
        // ボールの描画の計算処理
        if (time == 0L) {
            time = System.currentTimeMillis();
        }   // 最初のタイミングでは現在時刻を保存
        // イベントのセンサー識別の情報がアクセラメータ（加速度センサー）の時だけ以下の処理を実行
        if(isreturn == false){
        if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
            // センサーのx(左右),y(縦)値を取得
            val x = event.values[0] * -1;
            val y = event.values[1];

            // 経過時間を計算（今の時間-前の時間=経過時間）
            var t = (System.currentTimeMillis() - time).toFloat();
            // 今の時間を「前の時間」として保存
            time = System.currentTimeMillis();
            t /= 1000.0f;

            // 移動距離を計算（ボールをどれくらい動かすか）
            val dx = (vx * t) + (x * t * t) / 2.0f;   // xの移動距離(メートル)
            val dy = (vy * t) + (y * t * t) / 2.0f;   // yの移動距離(メートル)
            // メートルをピクセルのcmに補正してボールX座標に足しこむ
            ballX += (dx * coef);
            // メートルをピクセルのcmに補正してボールy座標に足しこむ
            ballY += (dy * coef);
            // 今の各方向の加速度を更新
            vx += (x * t);
            vy += (y * t);


            //  画面の端にきたら跳ね返るきのう
            // 左右
            if ((ballX - radius) < 0 && vx < 0) {
                // 左
                vx = -vx / 1.5f;
                ballX = radius;
            } else if ((ballX + radius) > surfaceWidth && vx > 0) {
                // 右
                vx = -vx / 1.5f;
                ballX = surfaceWidth - radius;
            }
            // 上下
            if ((ballY - radius) < 0 && vy < 0) {
                // 下
                vy = -vy / 1.5f;
                ballY = radius;
            } else if ((ballY + radius) > surfaceHeight && vy > 0) {
                // 上
                vy = -vy / 1.5f;
                ballY = surfaceHeight - radius;

            }
            var ballXr1 = ballX - radius;
            var ballYr1 = ballY - radius;
            var ballXr2 = ballX + radius;
            var ballYr2 = ballY + radius;
            var isbreak = false;
            //top400,bottom800,left600,right800
            if (ballXr1 < 800.0f && ballXr2 > 600.0f && ballYr2 > 400.0f && ballYr1 < 800.0f) {
                //失敗処理
                loseChange()
                isreturn = true;
            }
            //top,bottom,left,right
            if (ballXr1 < 700.0f && ballXr2 > 50.0f && ballYr2 > 250.0f && ballYr1 < 300.0f) {
                //失敗処理
                loseChange()
                isreturn = true;
            }
            //top,bottom,left,right
            if (ballXr1 < 200.0f && ballXr2 > 10.0f && ballYr2 > 550.0f && ballYr1 < 1000.0f) {
                //失敗処理
                loseChange()
                isreturn = true;
            }
            //top,bottom,left,right
            if (ballXr1 < 400.0f && ballXr2 > 100.0f && ballYr2 > 400.0f && ballYr1 < 500.0f) {
                //成功処理
                victoryChange()
                isreturn = true;
            }

            // キャンバスに描画
            this.drawCanvas();

            //ここに成功と失敗の処理。どちらかが行われた場合ボールが動かなくなる
        }
    }

    }

    //失敗時のテキストと画像の変更
    private fun loseChange(){
        imageView.setImageResource(R.drawable.sippai)
        txt_oen.setText("失敗...")

    }
    //成功時のテキストと画像の変更
    private fun victoryChange(){
        imageView.setImageResource(R.drawable.seiko)
        txt_oen.setText("成功!!")
    }

    // サーフェスが更新された時のイベント
    override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {
        // サーフェスの幅と高さをプロパティに保存しておく
        surfaceWidth = width;
        surfaceHeight = height;
        // ボールの初期位置を保存しておく
        ballX = (width/2).toFloat();
        ballY = 100.0f;

    }
    // サーフェスが破棄された時のイベント
    override fun surfaceDestroyed(p0: SurfaceHolder?) {
        // 加速度センサーの登録を解除する流れ
        // センサーマネージャを取得
        val sensorManager = this.getSystemService(Context.SENSOR_SERVICE) as SensorManager;
        // センサーマネージャーを通じてOSからリスナー（自分自身）を登録解除
        sensorManager.unregisterListener(this);
    }
    // サーフェスが作成された時のイベント
    override fun surfaceCreated(holder: SurfaceHolder?) {
        // 加速度センサーのリスナーを登録する流れ
        // センサーマネージャを取得
        val sensorManager = this.getSystemService(Context.SENSOR_SERVICE) as SensorManager;
        // センサーマネージャーからー加速度センサーを取得
        val accSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        // 加速度センサーのリスナーをOSに登録
        sensorManager.registerListener(
                this,   // リスナー（自クラス）
                accSensor,   // 加速度センサー
                SensorManager.SENSOR_DELAY_GAME   // センシングの頻度
        )
    }
    // サーフェスのキャンバスに描画するメソッド
    private fun drawCanvas(){
        // キャンバスをロックして取得
        val canvas = surfaceView.holder.lockCanvas();
        // キャンバスの背景色を設定
        canvas.drawColor(BLACK);
        // キャンバスに円を描いてボールにする
        canvas.drawCircle(
                ballX,   // ボール中心のX座標
                ballY,   // ボール中心のY座標
                radius,   // 半径
                Paint().apply { color = BLUE }   // ペイントブラシのインスタンス
        );
        canvas.drawRect(
                600.0f,
                400.0f,
                800.0f,
                800.0f,
                Paint().apply{color = GREEN})

        canvas.drawRect(
                50.0f,
                250.0f,
                700.0f,
                300.0f,
                Paint().apply{color = RED})

        canvas.drawRect(
                10.0f,
                550.0f,
                200.0f,
                1000.0f,
                Paint().apply{color = YELLOW})

        canvas.drawRect(
                100.0f,
                400.0f,
                400.0f,
                500.0f,
                Paint().apply{color = WHITE})


        // キャンバスをアンロック（ロック解除）
        surfaceView.holder.unlockCanvasAndPost(canvas);
    }

    private fun OnResetButton(){
        ballX = (surfaceWidth/2).toFloat();
        ballY = 100.0f;
        imageView.setImageResource(R.drawable.oen)
        txt_oen.setText("がんばれ！")
        isreturn = false;
        time = 0;
    }
}
