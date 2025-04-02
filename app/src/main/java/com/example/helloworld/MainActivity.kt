package com.example.helloworld

import android.annotation.SuppressLint
import android.text.InputFilter
import com.google.android.material.textfield.TextInputLayout
import android.view.View
import android.os.Bundle
import android.view.animation.AnimationUtils
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.TextView
import androidx.activity.ComponentActivity
import java.util.Calendar
import androidx.appcompat.app.AlertDialog

class MainActivity : ComponentActivity() {
    @SuppressLint("DefaultLocale", "MissingInflatedId", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_layout)

        val adultTicketPricePerUnit = 40
        val childTicketPricePerUnit = 20

        val firstPoster = findViewById<ImageButton>(R.id.first_poster)
        val firstDesc = findViewById<TextView>(R.id.first_desc)

        val secondPoster = findViewById<ImageButton>(R.id.second_poster)
        val secondDesc = findViewById<TextView>(R.id.second_desc)

        val fadeOut = AnimationUtils.loadAnimation(this, R.anim.fade_out)
        val fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in)

        val cinemaSpinner = findViewById<Spinner>(R.id.cinema_spinner)

        firstPoster.setOnClickListener {
            firstPoster.startAnimation(fadeOut)
            firstDesc.startAnimation(fadeIn)
            firstDesc.visibility = View.VISIBLE
            firstPoster.visibility = View.GONE
        }

        firstDesc.setOnClickListener {
            firstDesc.startAnimation(fadeOut)
            firstPoster.startAnimation(fadeIn)
            firstDesc.visibility = View.GONE
            firstPoster.visibility = View.VISIBLE
        }

        secondPoster.setOnClickListener {
            secondPoster.startAnimation(fadeOut)
            secondDesc.startAnimation(fadeIn)
            secondDesc.visibility = View.VISIBLE
            secondPoster.visibility = View.GONE
        }

        secondDesc.setOnClickListener {
            secondDesc.startAnimation(fadeOut)
            secondPoster.startAnimation(fadeIn)
            secondDesc.visibility = View.GONE
            secondPoster.visibility = View.VISIBLE
        }


        val adultTicketsSpinner = findViewById<Spinner>(R.id.adult_tickets_spinner)
        val childTicketsSpinner = findViewById<Spinner>(R.id.child_tickets_spinner)
        val adultTicketPrice = findViewById<TextView>(R.id.adult_ticket_price)
        val childTicketPrice= findViewById<TextView>(R.id.child_ticket_price)


        val ticketNumbers = ArrayAdapter.createFromResource(
            this,
            R.array.ticket_numbers,
            android.R.layout.simple_spinner_item
        )
        ticketNumbers.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        adultTicketsSpinner.adapter = ticketNumbers
        childTicketsSpinner.adapter = ticketNumbers


        // Update the ticket's price when the spinner item is selected
        adultTicketsSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            @SuppressLint("SetTextI18n")
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val adultTicketCount = parent.getItemAtPosition(position).toString().toInt()
                val totalAdultPrice = adultTicketCount * adultTicketPricePerUnit
                val adults = resources.getString(R.string.adults)
                adultTicketPrice.text = "$adultTicketCount $adults $totalAdultPrice₪"
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }


        childTicketsSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            @SuppressLint("SetTextI18n")
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val childTicketCount = parent.getItemAtPosition(position).toString().toInt()
                val totalChildPrice = childTicketCount * childTicketPricePerUnit
                val children = resources.getString(R.string.children)
                childTicketPrice.text = "$childTicketCount $children $totalChildPrice₪"
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        val screenType = findViewById<RadioGroup>(R.id.screen_type)
        var selectedScreenText = ""

        screenType.setOnCheckedChangeListener { _, checkedId ->
            selectedScreenText = when (checkedId) {
                R.id.two_d -> "2D"
                R.id.imax  -> "IMAX"
                R.id.three_d -> "3D"
                else -> "4DX"
            }
        }



        val dateBtn = findViewById<Button>(R.id.date_btn)

        dateBtn.setOnClickListener {
            val c = Calendar.getInstance()
            val minDate = c.timeInMillis
            c.add(Calendar.DAY_OF_YEAR, 31)
            val maxDate = c.timeInMillis
            c.add(Calendar.DAY_OF_YEAR, -31)

            val listener = android.app.DatePickerDialog.OnDateSetListener { _, year, month, day ->
                val selectedDate = String.format("%02d/%02d/%04d", day, month + 1, year)
                dateBtn.text = selectedDate
            }

            val dtd = android.app.DatePickerDialog(this, listener, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH))
            dtd.datePicker.minDate = minDate
            dtd.datePicker.maxDate = maxDate
            dtd.show()
        }

        val nameInputLayout = findViewById<TextInputLayout>(R.id.name_input_layout)
        val nameInput = findViewById<EditText>(R.id.name_input)

        fun lettersOnlyInputFilter(textInputLayout: TextInputLayout): InputFilter {
            return InputFilter { source, start, end, _, _, _ ->
                if (source == null) return@InputFilter null
                for (i in start until end) {
                    if (!Character.isLetter(source[i]) && source[i] != ' ') {
                        textInputLayout.error = resources.getString(R.string.only_letters)
                        return@InputFilter ""
                    }
                }
                textInputLayout.error = null
                null
            }
        }


        nameInput.filters = arrayOf(lettersOnlyInputFilter(nameInputLayout))

        fun showErrorDialog(message: String) {
            val errorView = layoutInflater.inflate(R.layout.dialog_layout, null)
            val errorDialog = AlertDialog.Builder(this, R.style.CustomAlertDialog).setView(errorView).create()
            errorDialog.setTitle(resources.getString(R.string.missing))
            errorView.findViewById<TextView>(R.id.MessageText).text = message
            errorView.findViewById<Button>(R.id.okButton)?.setOnClickListener {
                errorDialog.dismiss()
            }
            errorDialog.show()
        }

        val sumBtn = findViewById<Button>(R.id.sum_btn)
        sumBtn.setOnClickListener {
            val cinemasArray = resources.getStringArray(R.array.cinemas)
            val defaultCinema = cinemasArray[0]
            val defaultDate = getString(R.string.select_date)

            val finalName = nameInput.text.toString().trim()
            val date = dateBtn.text.toString()
            val selectedCinema = cinemaSpinner.selectedItem.toString()
            val adultTicketCount = adultTicketsSpinner.selectedItem.toString().toInt()
            val childTicketCount = childTicketsSpinner.selectedItem.toString().toInt()
            val totalAdultPrice = adultTicketCount * adultTicketPricePerUnit
            val totalChildPrice = childTicketCount * childTicketPricePerUnit
            val totalPrice = totalAdultPrice + totalChildPrice

            if (finalName.isEmpty()) {
                showErrorDialog(resources.getString(R.string.please_name))
            } else if (selectedCinema == defaultCinema) {
                showErrorDialog(resources.getString(R.string.please_cinema))
            } else if (date == defaultDate) {
                showErrorDialog(resources.getString(R.string.please_date))
            } else if (selectedScreenText.isEmpty()) {
                showErrorDialog(resources.getString(R.string.please_screen_type))
            } else if (adultTicketCount == 0 && childTicketCount == 0) {
                showErrorDialog(resources.getString(R.string.please_ticket))
            }
            else {
            val dialogView = layoutInflater.inflate(R.layout.dialog_layout, null)
            val dialog = AlertDialog.Builder(this).setView(dialogView).create()
            dialog.setTitle(resources.getString(R.string.orderSummary))

                val name = resources.getString(R.string.name)
                val cinema = resources.getString(R.string.cinema)
                val finalDate = resources.getString(R.string.date)
                val screen = resources.getString(R.string.screen_type)
                val adult = resources.getString(R.string.number_adult_tickets)
                val children = resources.getString(R.string.number_child_tickets)
                val total = resources.getString(R.string.total)


                val finalMessage = dialogView.findViewById<TextView>(R.id.MessageText)
                finalMessage.text = "$name $finalName\n$cinema $selectedCinema\n $finalDate $date\n" +
                        "$screen $selectedScreenText\n$adult $adultTicketCount\n" +
                        "$children $childTicketCount\n$total $totalPrice₪\n"


            dialogView.findViewById<Button>(R.id.okButton).setOnClickListener {
                dialog.dismiss()
            }

            dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
            dialog.show()
                }
        }

        val getTicketsBtn = findViewById<Button>(R.id.get_tickets)
        getTicketsBtn.setOnClickListener {
            val cinemasArray = resources.getStringArray(R.array.cinemas)
            val defaultCinema = cinemasArray[0]
            val defaultDate = getString(R.string.select_date)

            val finalName = nameInput.text.toString().trim()
            val date = dateBtn.text.toString()
            val selectedCinema = cinemaSpinner.selectedItem.toString()
            val adultTicketCount = adultTicketsSpinner.selectedItem.toString().toInt()
            val childTicketCount = childTicketsSpinner.selectedItem.toString().toInt()
            val totalAdultPrice = adultTicketCount * adultTicketPricePerUnit
            val totalChildPrice = childTicketCount * childTicketPricePerUnit
            val totalPrice = totalAdultPrice + totalChildPrice

            if (finalName.isEmpty()) {
                showErrorDialog(resources.getString(R.string.please_name))
            } else if (selectedCinema == defaultCinema) {
                showErrorDialog(resources.getString(R.string.please_cinema))
            } else if (date == defaultDate) {
                showErrorDialog(resources.getString(R.string.please_date))
            } else if (selectedScreenText.isEmpty()) {
                showErrorDialog(resources.getString(R.string.please_screen_type))
            } else if (adultTicketCount == 0 && childTicketCount == 0) {
                showErrorDialog(resources.getString(R.string.please_ticket))
            }
            else {
                val dialogView = layoutInflater.inflate(R.layout.dialog_layout, null)
                val dialog = AlertDialog.Builder(this).setView(dialogView).create()
                dialog.setTitle(resources.getString(R.string.confirmation))

                val name = resources.getString(R.string.name)
                val cinema = resources.getString(R.string.cinema)
                val finalDate = resources.getString(R.string.date)
                val screen = resources.getString(R.string.screen_type)
                val adult = resources.getString(R.string.number_adult_tickets)
                val children = resources.getString(R.string.number_child_tickets)
                val total = resources.getString(R.string.total)

                val finalMessage = dialogView.findViewById<TextView>(R.id.MessageText)
                finalMessage.text = "$name $finalName\n$cinema $selectedCinema\n $finalDate $date\n" +
                        "$screen $selectedScreenText\n$adult $adultTicketCount\n" +
                        "$children $childTicketCount\n$total $totalPrice₪\n"


                dialogView.findViewById<Button>(R.id.okButton).setOnClickListener {
                    dialog.dismiss()
                    // Reset all fields when order is confirmed
                    nameInput.setText("")
                    cinemaSpinner.setSelection(0)
                    adultTicketsSpinner.setSelection(0)
                    childTicketsSpinner.setSelection(0)
                    dateBtn.text = getString(R.string.select_date)
                    screenType.clearCheck()
                    adultTicketPrice.text = ""
                    childTicketPrice.text = ""
                }

                dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
                dialog.show()
            }
        }

    }
}


