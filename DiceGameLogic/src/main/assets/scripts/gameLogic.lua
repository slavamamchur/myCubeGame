-- Arguments are the activity and the view.
local sysUtilsWrapper = ...

local ROLLING_DICE_SOUND = 'rolling_dice.mp3'

startSound = function()
 	sysUtilsWrapper:iPlaySound(ROLLING_DICE_SOUND)
end

stopSound = function()
 	sysUtilsWrapper:iStopSound()
end

