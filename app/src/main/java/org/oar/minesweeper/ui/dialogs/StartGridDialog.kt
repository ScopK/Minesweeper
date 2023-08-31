package org.oar.minesweeper.ui.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.google.android.material.switchmaterial.SwitchMaterial
import org.oar.minesweeper.R
import org.oar.minesweeper.models.GridConfiguration
import org.oar.minesweeper.models.GridSettings
import org.oar.minesweeper.utils.PreferencesUtils.loadBoolean
import org.oar.minesweeper.utils.PreferencesUtils.save
import java.util.function.Consumer

class StartGridDialog(
    private val ctx: Context,
    private val gridConfig: GridConfiguration,
    private val confirm: Consumer<GridSettings>,
    private val cancel: Runnable = Runnable {}
) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val inflater = requireActivity().layoutInflater
        val view = inflater.inflate(R.layout.dialog_start_grid, null)

        val randomButton = view.findViewById<TextView>(R.id.randomGridButton)
        val solvableButton = view.findViewById<TextView>(R.id.solvableButton)

        randomButton.setOnClickListener {
            ctx.save("lastSolvable", false)

            confirm.accept(
                GridSettings(false, ctx.loadBoolean("lastVisualHelp", false))
            )
        }

        solvableButton.setOnClickListener {
            ctx.save("lastSolvable", true)

            confirm.accept(
                GridSettings(true, ctx.loadBoolean("lastVisualHelp", false))
            )
        }

        val title = String.format(getString(R.string.grid_summary_line),
            gridConfig.width, gridConfig.height, gridConfig.bombs)

        val builder = AlertDialog.Builder(activity)
        builder.setMessage(title)
            .setView(view)
            //.setNegativeButton(R.string.button_cancel) { _, _ -> cancel.run() }
            .setOnDismissListener { cancel.run() }

        return builder.show()
    }
}