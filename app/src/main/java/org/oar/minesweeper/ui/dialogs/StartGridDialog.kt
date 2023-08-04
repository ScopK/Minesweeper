package org.oar.minesweeper.ui.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.google.android.material.switchmaterial.SwitchMaterial
import org.oar.minesweeper.R
import org.oar.minesweeper.elements.GridConfiguration
import org.oar.minesweeper.elements.GridSettings
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

        val revealFirst = view.findViewById<SwitchMaterial>(R.id.revealFirst)
        val solvable = view.findViewById<SwitchMaterial>(R.id.solvable)

        revealFirst.isChecked = ctx.loadBoolean("lastRevealFirst", true)
        solvable.isChecked = ctx.loadBoolean("lastSolvable", false)

        solvable.setOnCheckedChangeListener { compoundButton,_ ->
            updateDependencies(revealFirst, compoundButton as SwitchMaterial)
        }
        updateDependencies(revealFirst, solvable)

        val title = String.format(getString(R.string.grid_summary_line),
            gridConfig.width, gridConfig.height, gridConfig.bombs)

        val builder = AlertDialog.Builder(activity)
        builder.setMessage(title)
            .setView(view)
            .setPositiveButton(R.string.button_confirm) { _, _ ->

                ctx.save("lastRevealFirst", revealFirst.isChecked)
                ctx.save("lastSolvable", solvable.isChecked)

                confirm.accept(GridSettings(
                    revealFirst.isChecked,
                    solvable.isChecked,
                    ctx.loadBoolean("lastVisualHelp", false)
                ))
            }
            .setNegativeButton(R.string.button_cancel) { _, _ -> cancel.run() }

        return builder.show()
    }

    private fun updateDependencies(revealFirst: SwitchMaterial, solvable: SwitchMaterial) {
        revealFirst.isEnabled = !solvable.isChecked
        if (solvable.isChecked) revealFirst.isChecked = true
    }
}