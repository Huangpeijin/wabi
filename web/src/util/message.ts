export function createChatBotMessage(text: string, isMe:boolean) {
    return {
        text,
        isMe,
    };
}