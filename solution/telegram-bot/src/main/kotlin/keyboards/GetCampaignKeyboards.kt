package ru.cwshbr.keyboards

import dev.inmo.tgbotapi.types.buttons.InlineKeyboardMarkup
import dev.inmo.tgbotapi.types.buttons.inline.dataInlineButton
import dev.inmo.tgbotapi.utils.matrix
import ru.cwshbr.models.CampaignModel

object GetCampaignKeyboards {
    fun generateCampaignListKeyboard(campaigns: List<CampaignModel>, ) = InlineKeyboardMarkup (
            matrix {  campaigns.map {  +dataInlineButton(it.adTitle,
                "getCampaign:"+it.toKeyboardCallbackFormat())  }
            }
    )


}