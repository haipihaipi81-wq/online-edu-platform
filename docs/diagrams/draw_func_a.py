import matplotlib
matplotlib.use('Agg')
import matplotlib.pyplot as plt
import matplotlib.patches as mpatches

plt.rcParams['font.sans-serif'] = ['SimSun', 'STSong', 'Arial Unicode MS', 'Heiti SC']
plt.rcParams['axes.unicode_minus'] = False

fig, ax = plt.subplots(1, 1, figsize=(10, 6))
ax.set_xlim(0, 10)
ax.set_ylim(0, 6)
ax.axis('off')

def draw_box(ax, x, y, w, h, text, bold=False, lw=1.2):
    rect = mpatches.FancyBboxPatch(
        (x - w/2, y - h/2), w, h,
        boxstyle="square,pad=0.15",
        facecolor='white', edgecolor='black', linewidth=lw
    )
    ax.add_patch(rect)
    fs = 8.5
    wt = 'bold' if bold else 'normal'
    ax.text(x, y, text, ha='center', va='center', fontsize=fs, fontweight=wt, color='black')

def v_line(ax, x, y1, y2):
    ax.plot([x, x], [y1, y2], color='black', lw=0.8)

def h_line(ax, x1, x2, y):
    ax.plot([x1, x2], [y, y], color='black', lw=0.8)

# ===== 顶部：系统总称 =====
draw_box(ax, 5.0, 5.55, 4.0, 0.55, '在线教育平台', bold=True, lw=1.5)

v_line(ax, 5.0, 5.27, 4.95)
h_line(ax, 2.6, 7.4, 4.95)

mod_x = [2.6, 7.4]
for mx in mod_x:
    v_line(ax, mx, 4.95, 4.55)

# ===== 两大模块 =====
draw_box(ax, 2.6, 4.2, 3.8, 0.55, '用户与认证模块', bold=True)
draw_box(ax, 7.4, 4.2, 3.8, 0.55, '课程与内容管理模块', bold=True)

for mx in mod_x:
    v_line(ax, mx, 3.92, 3.6)

h_line(ax, 2.6, 7.4, 3.6)

# ===== 用户与认证子功能 =====
user_children = [
    '用户注册\n与登录',
    'JWT 令牌\n鉴权',
    '角色权限\n控制',
    '用户信息\n管理',
]
user_n = len(user_children)
user_spacing = 3.6 / (user_n + 1)
for i, child in enumerate(user_children):
    cx = 2.6 - 1.8 + user_spacing * (i + 1)
    v_line(ax, cx, 3.6, 3.25)
    draw_box(ax, cx, 3.0, 1.4, 0.48, child)

# ===== 课程与内容管理子功能 =====
course_children = [
    '课程分类\n管理',
    '课程发布\n与编辑',
    '章节课时\n编排',
    '选课与进度\n追踪',
]
course_n = len(course_children)
course_spacing = 3.6 / (course_n + 1)
for i, child in enumerate(course_children):
    cx = 7.4 - 1.8 + course_spacing * (i + 1)
    v_line(ax, cx, 3.6, 3.25)
    draw_box(ax, cx, 3.0, 1.4, 0.48, child)

# ===== 分工虚线框 =====
ax.plot([0.5, 10.0, 10.0, 0.5, 0.5], [5.0, 5.0, 2.3, 2.3, 5.0],
        'k--', lw=0.8)
ax.text(9.5, 2.5, 'Person A 负责', ha='center', va='center',
        fontsize=8, fontweight='bold', color='black',
        bbox=dict(boxstyle='square,pad=0.3', facecolor='white',
                   edgecolor='black', linewidth=0.8, linestyle='--'))

# ===== 图注 =====
ax.text(5.0, 0.15, '图2-2  系统功能架构设计图（Person A）', ha='center', va='center',
        fontsize=10, fontweight='bold', color='black')

plt.tight_layout(pad=0.3)
plt.savefig('/Users/qt/online-edu-platform/docs/diagrams/功能结构图_PersonA.png',
            dpi=200, bbox_inches='tight', facecolor='white', edgecolor='none')
plt.close()
print('Done')
