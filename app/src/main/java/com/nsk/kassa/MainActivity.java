package com.nsk.kassa;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import ru.atol.drivers10.fptr.Fptr;
import ru.atol.drivers10.fptr.IFptr;
public class MainActivity extends AppCompatActivity {
    private Spinner serviceSpinner;
    private EditText sumaEditText;
    private Button payButton;
    private Button repotButton;

    public class Printer {
        private IFptr fptr;

        public Printer(Context context) {
            fptr = new Fptr(context.getApplicationContext());
            fptr.setSingleSetting(IFptr.LIBFPTR_SETTING_MODEL, String.valueOf(IFptr.LIBFPTR_MODEL_ATOL_AUTO));
            fptr.setSingleSetting(IFptr.LIBFPTR_SETTING_PORT, String.valueOf(IFptr.LIBFPTR_PORT_TCPIP));
            fptr.setSingleSetting(IFptr.LIBFPTR_SETTING_IPADDRESS,  "0.0.0.0");
            fptr.setSingleSetting(IFptr.LIBFPTR_SETTING_IPPORT,  "5555");
            fptr.applySingleSettings();
        }

        public void openPrinter() {
            fptr.open();
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Находим элементы пользовательского интерфейса по ID
        serviceSpinner = findViewById(R.id.service_spinner);
        sumaEditText = findViewById(R.id.suma_edit_text);
        payButton = findViewById(R.id.pay_button);
        repotButton = findViewById(R.id.btn_report);
        // Создаем массив с названиями услуг для выпадающего списка
        String[] services = {"Услуга 1", "Услуга 2", "Услуга 3", "Услуга 4"};

        // Создаем адаптер для выпадающего списка
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, services);

        // Устанавливаем адаптер для выпадающего списка
        serviceSpinner.setAdapter(adapter);

        // Устанавливаем обработчик нажатия на кнопку "Оплатить"
        payButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Получаем значения полей ввода
                String serviceName = serviceSpinner.getSelectedItem().toString();
                String suma = sumaEditText.getText().toString();

                // Проверяем, что все поля заполнены
                if (suma.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Заполните все поля", Toast.LENGTH_SHORT).show();
                } else {
                    // Выполняем оплату
                    //Toast.makeText(MainActivity.this, "Оплачено: " + serviceName, Toast.LENGTH_SHORT).show();
                    IFptr fptr = new Fptr(getApplicationContext());
                    fptr.setSingleSetting(IFptr.LIBFPTR_SETTING_MODEL, String.valueOf(IFptr.LIBFPTR_MODEL_ATOL_AUTO));
                    fptr.setSingleSetting(IFptr.LIBFPTR_SETTING_PORT, String.valueOf(IFptr.LIBFPTR_PORT_TCPIP));
                    fptr.setSingleSetting(IFptr.LIBFPTR_SETTING_IPADDRESS,  "0.0.0.0");
                    fptr.setSingleSetting(IFptr.LIBFPTR_SETTING_IPPORT,  "5555");
                    fptr.applySingleSettings();
                    fptr.open();
                    boolean isOpened = fptr.isOpened();
                    if (isOpened) {
                        //Toast.makeText(MainActivity.this, "Подключено", Toast.LENGTH_SHORT).show();
                        fptr.setParam(1021, "Кассир Иванов И.");
                        fptr.setParam(1203, "123456789047");
                        fptr.operatorLogin();

                        fptr.setParam(IFptr.LIBFPTR_PARAM_RECEIPT_TYPE, IFptr.LIBFPTR_RT_SELL);
                        fptr.openReceipt();

                        fptr.setParam(IFptr.LIBFPTR_PARAM_COMMODITY_NAME, serviceName);
                        fptr.setParam(IFptr.LIBFPTR_PARAM_PRICE, 1);
                        fptr.setParam(IFptr.LIBFPTR_PARAM_QUANTITY, Double.parseDouble(suma));
                        fptr.setParam(IFptr.LIBFPTR_PARAM_TAX_TYPE, IFptr.LIBFPTR_TAX_VAT0);
                        fptr.registration();

                        fptr.setParam(IFptr.LIBFPTR_PARAM_PAYMENT_TYPE, IFptr.LIBFPTR_PT_CASH);
                        fptr.setParam(IFptr.LIBFPTR_PARAM_PAYMENT_SUM, Double.parseDouble(suma));
                        fptr.payment();

                        fptr.closeReceipt();

                    }
                    fptr.close();
                }
            }
        });

        repotButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, Report.class);
                startActivity(intent);
            }
        });


    }
}