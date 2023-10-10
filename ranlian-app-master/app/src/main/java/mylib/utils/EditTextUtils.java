package mylib.utils;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

public class EditTextUtils {
    /**
     * 设置edittext只能输入小数点后两位
     */
    public static void afterDotTwo(final EditText editText) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // 限制最多能输入9位整数
                if (s.toString().contains(".")) {
                    if (s.toString().indexOf(".") > 9) {
                        s = s.toString().subSequence(0, 9) + s.toString().substring(s.toString().indexOf("."));
                        editText.setText(s);
                        editText.setSelection(9);
                    }
                } else {
                    if (s.toString().length() > 9) {
                        s = s.toString().subSequence(0, 9);
                        editText.setText(s);
                        editText.setSelection(9);
                    }
                }
                // 判断小数点后只能输入两位
                if (s.toString().contains(".")) {
                    if (s.length() - 1 - s.toString().indexOf(".") > 2) {
                        s = s.toString().subSequence(0,
                                s.toString().indexOf(".") + 3);
                        editText.setText(s);
                        editText.setSelection(s.length());
                    }
                }

                /**
                 * 下面代码放开后
                 * 我这里写的判断不是很好，之后可以改进。
                 * 这个特殊情况是这样的，假如你输入的第一位是0，你想想，你能输入0233，05这种数字吗，就是您能，这样展示给用户的效果也很不友好，
                 * 所以要判断如果第一位数是0，第二位不是小数点的话，就输出0。
                 * 这步的算法有两个问题，
                 * 第一，如果在0.26的情况下，我把光标点在0后面，输入一个不为0的数字假设5，得到的结果是0而不是5.26。
                 * 第二，假如还是0.26的情况下，我把光标点在小数点后面，删除小数点，得到的不是26而是0。
                 */
                //如果第一个数字为0，第二个不为点，就不允许输入
//                if (s.toString().startsWith("0") && s.toString().trim().length() >  1) {
//                    if (!s.toString().substring(1, 2).equals(".")) {
//                        editText.setText(s.subSequence(0, 1));
//                        editText.setSelection(1);
//                        return;
//                    }
//                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (editText.getText().toString().trim() != null && !editText.getText().toString().trim().equals("")) {
                    if (editText.getText().toString().trim().substring(0, 1).equals(".")) {
                        editText.setText("0" + editText.getText().toString().trim());
                        editText.setSelection(1);
                    }
                }
            }
        });
    }
}
