/*
 * MIT License
 *
 * Copyright (c) 2018 - 2022 CDAGaming (cstack2011@yahoo.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.gitlab.cdagaming.craftpresence.utils.discord.rpc.entities;

/**
 * A encapsulation of a Discord User's data provided when a
 * {@link com.gitlab.cdagaming.craftpresence.utils.discord.rpc.IPCListener IPCListener} fires
 * {@link com.gitlab.cdagaming.craftpresence.utils.discord.rpc.IPCListener#onActivityJoinRequest(com.gitlab.cdagaming.craftpresence.utils.discord.rpc.IPCClient, String, User)
 * onActivityJoinRequest}.
 *
 * @author John Grosh (john.a.grosh@gmail.com)
 */
public class User {
    private final String name;
    private final String discriminator;
    private final long id;
    private final String avatar;

    /**
     * Constructs a new {@link User}.<br>
     * Only implemented internally.
     *
     * @param name          user's name
     * @param discriminator user's discriminator
     * @param id            user's id
     * @param avatar        user's avatar hash, or {@code null} if they have no avatar
     */
    public User(String name, String discriminator, long id, String avatar) {
        this.name = name;
        this.discriminator = discriminator;
        this.id = id;
        this.avatar = avatar;
    }

    /**
     * Gets the Users account name.
     *
     * @return The Users account name.
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the Users discriminator.
     *
     * @return The Users discriminator.
     */
    public String getDiscriminator() {
        return discriminator;
    }

    /**
     * Gets the Users Snowflake ID as a {@code long}.
     *
     * @return The Users Snowflake ID as a {@code long}.
     */
    public long getIdLong() {
        return id;
    }

    /**
     * Gets the Users Snowflake ID as a {@code String}.
     *
     * @return The Users Snowflake ID as a {@code String}.
     */
    public String getId() {
        return Long.toString(id);
    }

    /**
     * Gets the Users avatar ID.
     *
     * @return The Users avatar ID.
     */
    public String getAvatarId() {
        return avatar;
    }

    /**
     * Gets the Users avatar URL.
     *
     * @return The Users avatar URL.
     */
    public String getAvatarUrl() {
        return getAvatarId() == null ? null : "https://cdn.discordapp.com/avatars/" + getId() + "/" + getAvatarId()
                + (getAvatarId().startsWith("a_") ? ".gif" : ".png");
    }

    /**
     * Gets the Users {@link DefaultAvatar} avatar ID.
     *
     * @return The Users {@link DefaultAvatar} avatar ID.
     */
    public String getDefaultAvatarId() {
        return DefaultAvatar.values()[Integer.parseInt(getDiscriminator()) % DefaultAvatar.values().length].toString();
    }

    /**
     * Gets the Users {@link DefaultAvatar} avatar URL.
     *
     * @return The Users {@link DefaultAvatar} avatar URL.
     */
    public String getDefaultAvatarUrl() {
        return "https://discord.com/assets/" + getDefaultAvatarId() + ".png";
    }

    /**
     * Gets the Users avatar URL, or their {@link DefaultAvatar} avatar URL if they
     * do not have a custom avatar set on their account.
     *
     * @return The Users effective avatar URL.
     */
    public String getEffectiveAvatarUrl() {
        return getAvatarUrl() == null ? getDefaultAvatarUrl() : getAvatarUrl();
    }

    /**
     * Gets whether or not this User is a bot.
     * <p>
     * While, at the time of writing this documentation, bots cannot
     * use Rich Presence features, there may be a time in the future
     * where they have such an ability.
     *
     * @return False
     */
    public boolean isBot() {
        return false; //bots cannot use RPC
    }

    /**
     * Gets the User as a discord formatted mention.
     * <p>
     * {@code <@SNOWFLAKE_ID> }
     *
     * @return A discord formatted mention of this User.
     */
    public String getAsMention() {
        return "<@" + id + '>';
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof User))
            return false;
        User oUser = (User) o;
        return this == oUser || this.id == oUser.id;
    }

    @Override
    public int hashCode() {
        return Long.hashCode(id);
    }

    @Override
    public String toString() {
        return "U:" + getName() + '(' + id + ')';
    }

    /**
     * Constants representing one of five different
     * default avatars a {@link User} can have.
     */
    public enum DefaultAvatar {
        BLURPLE("6debd47ed13483642cf09e832ed0bc1b"),
        GREY("322c936a8c8be1b803cd94861bdfa868"),
        GREEN("dd4dbc0016779df1378e7812eabaa04d"),
        ORANGE("0e291f67c9274a1abdddeb3fd919cbaa"),
        RED("1cbd08c76f8af6dddce02c5138971129");

        private final String text;

        DefaultAvatar(String text) {
            this.text = text;
        }

        @Override
        public String toString() {
            return text;
        }
    }
}
